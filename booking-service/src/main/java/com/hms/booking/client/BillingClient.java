package com.hms.booking.client;

import com.hms.booking.config.ClientsProperties;
import com.hms.common.dto.billing.CreateInvoiceFromBookingRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BillingClient {

    private final RestTemplate restTemplate;
    private final ClientsProperties props;

    public BillingClient(RestTemplate restTemplate, ClientsProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public void createInvoiceFromBooking(CreateInvoiceFromBookingRequest req) {
        String url = props.getGatewayBaseUrl() + "/api/internal/invoices/from-booking";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Key", props.getInternalKey());
        headers.set("Content-Type", "application/json");

        HttpEntity<CreateInvoiceFromBookingRequest> entity = new HttpEntity<>(req, headers);
        try {
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            System.err.println("Failed to create invoice: " + e.getMessage());
        }
    }

    public void deleteInvoiceByBookingId(Long bookingId) {
        String url = props.getGatewayBaseUrl() + "/api/internal/invoices/by-booking/" + bookingId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Key", props.getInternalKey());

        HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(url, org.springframework.http.HttpMethod.DELETE, entity, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to delete invoice: " + e.getMessage());
            throw e;
        }
    }
}
