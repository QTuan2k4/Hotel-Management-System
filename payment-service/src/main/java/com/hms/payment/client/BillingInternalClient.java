package com.hms.payment.client;

import com.hms.common.dto.billing.InvoiceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BillingInternalClient {

  private final RestTemplate restTemplate;

  @Value("${clients.gatewayBaseUrl}")
  private String gatewayBaseUrl;

  @Value("${clients.internalKey}")
  private String internalKey;

  public BillingInternalClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public InvoiceDto getInvoice(Long invoiceId) {
    String url = gatewayBaseUrl + "/api/internal/invoices/" + invoiceId;
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Internal-Key", internalKey);

    HttpEntity<?> entity = new HttpEntity<>(headers);
    try {
      ResponseEntity<InvoiceDto> resp = restTemplate.exchange(url, HttpMethod.GET, entity, InvoiceDto.class);
      return resp.getBody();
    } catch (Exception e) {
      return null;
    }
  }

  public void markPaid(Long invoiceId) {
    String url = gatewayBaseUrl + "/api/internal/invoices/" + invoiceId + "/mark-paid";
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Internal-Key", internalKey);

    HttpEntity<?> entity = new HttpEntity<>(headers);
    try {
      restTemplate.postForEntity(url, entity, String.class);
    } catch (Exception e) {
      System.err.println("Failed to mark invoice paid: " + e.getMessage());
    }
  }
}
