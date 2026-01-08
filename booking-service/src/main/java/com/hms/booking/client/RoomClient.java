package com.hms.booking.client;

import com.hms.booking.config.ClientsProperties;
import com.hms.common.dto.room.RoomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class RoomClient {

    private final RestTemplate restTemplate;
    private final ClientsProperties props;

    public RoomClient(RestTemplate restTemplate, ClientsProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public BigDecimal getPricePerNight(Long roomId) {
        String url = props.getGatewayBaseUrl() + "/api/rooms/" + roomId;
        try {
            ResponseEntity<RoomDto> resp = restTemplate.getForEntity(url, RoomDto.class);
            if (resp.getBody() != null && resp.getBody().getPricePerNight() != null) {
                return resp.getBody().getPricePerNight();
            }
        } catch (Exception e) {
            System.err.println("Failed to get room price: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
}
