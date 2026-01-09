package com.hms.payment.service;

import com.hms.payment.config.VnPayConfig;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VnPayService {

    private final VnPayConfig config;

    public VnPayService(VnPayConfig config) {
        this.config = config;
    }

    /**
     * Create VNPay payment URL
     */
    public String createPaymentUrl(String txnRef, long amount, String orderInfo, String ipAddress) {
        Map<String, String> params = new HashMap<>();

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", config.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay requires amount * 100
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", config.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        params.put("vnp_CreateDate", now.format(formatter));
        params.put("vnp_ExpireDate", now.plusMinutes(15).format(formatter));

        // Sort params by key and build query string
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder hashDataBuilder = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // Build hash data
                hashDataBuilder.append(fieldName).append("=")
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII))
                        .append("&");
                // Build query string
                queryBuilder.append(fieldName).append("=")
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII))
                        .append("&");
            }
        }

        // Remove trailing &
        String hashData = hashDataBuilder.substring(0, hashDataBuilder.length() - 1);
        String query = queryBuilder.substring(0, queryBuilder.length() - 1);

        // Create HMAC SHA512 signature
        String secureHash = hmacSHA512(config.getHashSecret(), hashData);

        return config.getUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    /**
     * Validate callback from VNPay
     * Note: Spring already URL-decodes the query params, so we need to re-encode
     * them
     * exactly as VNPay expects for the hash calculation
     */
    public boolean validateCallback(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        if (vnp_SecureHash == null) {
            System.err.println("VNPay callback missing vnp_SecureHash");
            return false;
        }

        // Remove hash fields
        Map<String, String> fields = new HashMap<>(params);
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        // Sort and build hash data
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashDataBuilder = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (hashDataBuilder.length() > 0) {
                    hashDataBuilder.append("&");
                }
                // URL encode the value for hash calculation (VNPay uses URL-encoded values for
                // hash)
                hashDataBuilder.append(fieldName).append("=")
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            }
        }

        String hashData = hashDataBuilder.toString();
        String calculatedHash = hmacSHA512(config.getHashSecret(), hashData);

        boolean valid = calculatedHash.equalsIgnoreCase(vnp_SecureHash);
        if (!valid) {
            System.err.println("VNPay hash mismatch!");
            System.err.println("Expected: " + vnp_SecureHash);
            System.err.println("Calculated: " + calculatedHash);
            System.err.println("Hash data: " + hashData);
        }
        return valid;
    }

    /**
     * Check if payment was successful
     */
    public boolean isPaymentSuccess(Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        return "00".equals(responseCode) && "00".equals(transactionStatus);
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKeySpec);
            byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error creating HMAC SHA512", e);
        }
    }
}
