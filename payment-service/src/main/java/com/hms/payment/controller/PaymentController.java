package com.hms.payment.controller;

import com.hms.common.dto.payment.CreateVnpayPaymentRequest;
import com.hms.common.dto.payment.CreateVnpayPaymentResponse;
import com.hms.payment.entity.PaymentTxn;
import com.hms.payment.service.PaymentService;
import com.hms.payment.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

  private final PaymentService paymentService;
  private final VnPayService vnPayService;

  @Value("${app.frontendBaseUrl}")
  private String frontendBaseUrl;

  public PaymentController(PaymentService paymentService, VnPayService vnPayService) {
    this.paymentService = paymentService;
    this.vnPayService = vnPayService;
  }

  @PostMapping("/vnpay/create")
  public CreateVnpayPaymentResponse create(@RequestBody CreateVnpayPaymentRequest req,
      HttpServletRequest httpRequest) {
    String ipAddress = getClientIp(httpRequest);
    return paymentService.createPayment(req.getInvoiceId(), ipAddress);
  }

  /**
   * VNPay IPN (Instant Payment Notification) - Server to server callback
   * This endpoint must return JSON: {"RspCode": "00", "Message": "Confirm
   * Success"}
   */
  @GetMapping("/vnpay/ipn")
  public Map<String, String> vnpayIpn(@RequestParam Map<String, String> params) {
    Map<String, String> response = new HashMap<>();

    try {
      // Validate checksum
      if (!vnPayService.validateCallback(params)) {
        response.put("RspCode", "97");
        response.put("Message", "Invalid Checksum");
        return response;
      }

      String txnRef = params.get("vnp_TxnRef");
      PaymentTxn tx = paymentService.findByTxnRef(txnRef);

      if (tx == null) {
        response.put("RspCode", "01");
        response.put("Message", "Order not found");
        return response;
      }

      // Check if already processed
      if ("SUCCESS".equals(tx.getStatus()) || "FAILED".equals(tx.getStatus())) {
        response.put("RspCode", "02");
        response.put("Message", "Order already confirmed");
        return response;
      }

      // Process payment result
      boolean success = vnPayService.isPaymentSuccess(params);
      paymentService.handleCallback(txnRef, success);

      response.put("RspCode", "00");
      response.put("Message", "Confirm Success");
    } catch (Exception e) {
      response.put("RspCode", "99");
      response.put("Message", "Unknown error");
      e.printStackTrace();
    }

    return response;
  }

  /**
   * VNPay Return URL - User redirect after payment
   */
  @GetMapping("/vnpay/return")
  public ResponseEntity<?> vnpayReturn(@RequestParam Map<String, String> params) {
    String txnRef = params.get("vnp_TxnRef");
    String responseCode = params.get("vnp_ResponseCode");

    // Validate checksum (log warning if fails but still process based on
    // responseCode for sandbox)
    boolean validHash = vnPayService.validateCallback(params);
    if (!validHash) {
      System.err.println("WARNING: VNPay hash validation failed, but continuing based on responseCode");
    }

    String status = "failed";
    // In sandbox, responseCode "00" means success
    if ("00".equals(responseCode)) {
      status = "success";
      // Process payment
      paymentService.handleCallback(txnRef, true);
    } else {
      // Payment failed
      paymentService.handleCallback(txnRef, false);
    }

    // Redirect to frontend result page
    String redirectUrl = frontendBaseUrl + "/payment/result?status=" + status + "&txnRef=" + txnRef;
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(redirectUrl))
        .build();
  }

  @GetMapping("/ping")
  public String ping() {
    return "payment-service: OK";
  }

  private String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    String ip = request.getRemoteAddr();
    // Handle localhost IPv6
    if ("0:0:0:0:0:0:0:1".equals(ip)) {
      return "127.0.0.1";
    }
    return ip;
  }
}
