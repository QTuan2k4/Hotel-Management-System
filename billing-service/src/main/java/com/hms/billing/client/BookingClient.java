package com.hms.billing.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client to communicate with booking-service for status updates
 */
@Component
public class BookingClient {

    private final RestTemplate restTemplate;

    @Value("${clients.bookingServiceBaseUrl:http://localhost:9003}")
    private String bookingServiceBaseUrl;

    public BookingClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Confirm booking after payment is successful
     */
    public void confirmBooking(Long bookingId) {
        try {
            String url = bookingServiceBaseUrl + "/internal/bookings/" + bookingId + "/confirm";
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to confirm booking " + bookingId + ": " + e.getMessage());
        }
    }
}
