package com.hms.payment.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hms.common.dto.billing.InvoiceDto;
import com.hms.common.dto.payment.CreateVnpayPaymentResponse;
import com.hms.payment.client.BillingInternalClient;
import com.hms.payment.entity.PaymentTxn;
import com.hms.payment.repository.PaymentTxnRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

  private final PaymentTxnRepository repo;
  private final BillingInternalClient billing;

  @Value("${clients.gatewayBaseUrl}")
  private String gatewayBaseUrl;

  public PaymentService(PaymentTxnRepository repo, BillingInternalClient billing) {
    this.repo = repo;
    this.billing = billing;
  }

  @Transactional
  public CreateVnpayPaymentResponse createPayment(Long invoiceId) {
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

    // Mock URL for testing: clicking this simulates successful payment
    String paymentUrl = gatewayBaseUrl + "/api/payments/vnpay/mock-success?txnRef=" + txnRef;
    return new CreateVnpayPaymentResponse(paymentUrl, txnRef);
  }

  @Transactional
  public void handleCallback(String txnRef, String result) {
    PaymentTxn tx = repo.findByTxnRef(txnRef).orElseThrow();

    if ("success".equalsIgnoreCase(result)) {
      tx.setStatus("SUCCESS");
      tx.setPaidAt(LocalDateTime.now());
      repo.save(tx);
      billing.markPaid(tx.getInvoiceId());
    } else {
      tx.setStatus("FAILED");
      repo.save(tx);
    }
  }
}
