package com.hms.payment.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hms.common.dto.billing.InvoiceDto;
import com.hms.common.dto.payment.CreateVnpayPaymentResponse;
import com.hms.payment.client.BillingInternalClient;
import com.hms.payment.client.BookingInternalClient;
import com.hms.payment.entity.PaymentTxn;
import com.hms.payment.repository.PaymentTxnRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

  private final PaymentTxnRepository repo;
  private final BillingInternalClient billing;
  private final BookingInternalClient bookingClient;
  private final VnPayService vnPayService;

  @Value("${clients.gatewayBaseUrl}")
  private String gatewayBaseUrl;

  public PaymentService(PaymentTxnRepository repo, BillingInternalClient billing,
      BookingInternalClient bookingClient, VnPayService vnPayService) {
    this.repo = repo;
    this.billing = billing;
    this.bookingClient = bookingClient;
    this.vnPayService = vnPayService;
  }

  @Transactional
  public CreateVnpayPaymentResponse createPayment(Long invoiceId, String ipAddress) {
    InvoiceDto inv = billing.getInvoice(invoiceId);
    if (inv == null)
      throw new IllegalStateException("Invoice not found");

    String txnRef = "TXN" + System.currentTimeMillis();

    PaymentTxn tx = PaymentTxn.builder()
        .invoiceId(invoiceId)
        .provider("VNPAY")
        .txnRef(txnRef)
        .amount(inv.getTotal())
        .status("INITIATED")
        .createdAt(LocalDateTime.now())
        .build();
    repo.save(tx);

    // Store bookingId for later cancellation if needed
    // Note: We can get this from invoice when handling callback

    // Create real VNPay URL
    String orderInfo = "Thanh toan hoa don " + invoiceId;
    String paymentUrl = vnPayService.createPaymentUrl(txnRef, inv.getTotal().longValue(), orderInfo, ipAddress);

    return new CreateVnpayPaymentResponse(paymentUrl, txnRef);
  }

  @Transactional
  public void handleCallback(String txnRef, boolean success) {
    PaymentTxn tx = repo.findByTxnRef(txnRef).orElse(null);
    if (tx == null) {
      System.err.println("Transaction not found: " + txnRef);
      return;
    }

    // Avoid duplicate processing
    if ("SUCCESS".equals(tx.getStatus()) || "FAILED".equals(tx.getStatus())) {
      System.out.println("Transaction already processed: " + txnRef);
      return;
    }

    if (success) {
      tx.setStatus("SUCCESS");
      tx.setPaidAt(LocalDateTime.now());
      repo.save(tx);
      billing.markPaid(tx.getInvoiceId());
      
      // Confirm booking
      try {
          InvoiceDto inv = billing.getInvoice(tx.getInvoiceId());
          if (inv != null && inv.getBookingId() != null) {
              bookingClient.confirmBooking(inv.getBookingId());
          }
      } catch (Exception e) {
          System.err.println("Failed to confirm booking after payment success: " + e.getMessage());
      }
    } else {
      tx.setStatus("FAILED");
      repo.save(tx);

      // Cancel booking and notify user
      try {
        InvoiceDto inv = billing.getInvoice(tx.getInvoiceId());
        if (inv != null && inv.getBookingId() != null) {
          bookingClient.cancelDueToPaymentFailure(inv.getBookingId());
        }
      } catch (Exception e) {
        System.err.println("Failed to cancel booking after payment failure: " + e.getMessage());
      }
    }
  }

  public PaymentTxn findByTxnRef(String txnRef) {
    return repo.findByTxnRef(txnRef).orElse(null);
  }
}
