package com.hms.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class RoomClient {

    private final RestTemplate restTemplate;

    @Value("${clients.gatewayBaseUrl:http://localhost:8081}")
    private String gatewayBaseUrl;

    public RoomClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal getPricePerNight(Long roomId) {
        String url = gatewayBaseUrl + "/api/rooms/" + roomId;
        RoomSummary room = restTemplate.getForObject(url, RoomSummary.class);
        if (room == null || room.getPricePerNight() == null) {
            throw new IllegalStateException("Cannot fetch room price for roomId=" + roomId);
        }
        return room.getPricePerNight();
    }
}
