package com.hms.payment.controller;

import com.hms.common.dto.payment.CreateVnpayPaymentRequest;
import com.hms.common.dto.payment.CreateVnpayPaymentResponse;
import com.hms.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

  private final PaymentService paymentService;

  @Value("${app.frontendBaseUrl}")
  private String frontendBaseUrl;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping("/vnpay/create")
  public CreateVnpayPaymentResponse create(@RequestBody CreateVnpayPaymentRequest req) {
    return paymentService.createPayment(req.getInvoiceId());
  }

  @GetMapping("/vnpay/mock-success")
  public ResponseEntity<?> mockSuccess(@RequestParam String txnRef) {
    paymentService.handleCallback(txnRef, "success");
    // Redirect to frontend payment result page
    String redirectUrl = frontendBaseUrl + "/payment/result?status=success&txnRef=" + txnRef;
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(redirectUrl))
        .build();
  }

  @GetMapping("/ping")
  public String ping() {
    return "payment-service: OK";
  }
}
