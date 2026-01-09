package com.hms.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BookingInternalClient {

    private final RestTemplate restTemplate;

    @Value("${clients.gatewayBaseUrl}")
    private String gatewayBaseUrl;

    @Value("${clients.internalKey}")
    private String internalKey;

    public BookingInternalClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Confirm booking after payment success
     */
    public void confirmBooking(Long bookingId) {
        String url = gatewayBaseUrl + "/api/internal/bookings/" + bookingId + "/confirm";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Key", internalKey);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
            System.out.println("Booking " + bookingId + " confirmed after payment success");
        } catch (Exception e) {
            System.err.println("Failed to confirm booking " + bookingId + ": " + e.getMessage());
        }
    }

    /**
     * Cancel booking due to payment failure
     */
    public void cancelDueToPaymentFailure(Long bookingId) {
        String url = gatewayBaseUrl + "/api/internal/bookings/" + bookingId + "/cancel-payment-failed";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Key", internalKey);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
            System.out.println("Booking " + bookingId + " cancelled due to payment failure");
        } catch (Exception e) {
            System.err.println("Failed to cancel booking " + bookingId + ": " + e.getMessage());
        }
    }
}
