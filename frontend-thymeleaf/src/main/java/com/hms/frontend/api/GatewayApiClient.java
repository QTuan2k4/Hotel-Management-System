package com.hms.frontend.api;

import com.hms.common.dto.booking.BookingDto;
import com.hms.common.dto.booking.CreateBookingRequest;
import com.hms.frontend.config.FrontendProperties;
import com.hms.frontend.session.SessionAuth;


import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class GatewayApiClient {

    private final RestTemplate restTemplate;
    private final FrontendProperties props;

    public GatewayApiClient(RestTemplate restTemplate, FrontendProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public <T> T get(String path, Class<T> type, SessionAuth auth) {
        return exchange(path, HttpMethod.GET, null, type, auth).getBody();
    }

    public <T> T post(String path, Object body, Class<T> type, SessionAuth auth) {
        return exchange(path, HttpMethod.POST, body, type, auth).getBody();
    }

    public <T> T put(String path, Object body, Class<T> type, SessionAuth auth) {
        return exchange(path, HttpMethod.PUT, body, type, auth).getBody();
    }

    public void delete(String path, SessionAuth auth) {
        exchange(path, HttpMethod.DELETE, null, String.class, auth);
    }

    private <T> ResponseEntity<T> exchange(String path, HttpMethod method, Object body, Class<T> type, SessionAuth auth) {
        String url = props.getGatewayBaseUrl() + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (auth != null && auth.isLoggedIn()) {
            headers.setBearerAuth(auth.getToken());
        }

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, method, entity, type);
    }
    
    public BookingDto createBooking(Long roomId, String checkInDate, String checkOutDate, SessionAuth auth) {
        CreateBookingRequest req = new CreateBookingRequest();
        req.setRoomId(roomId);
        req.setCheckInDate(java.time.LocalDate.parse(checkInDate));
        req.setCheckOutDate(java.time.LocalDate.parse(checkOutDate));

        return post("/api/bookings", req, BookingDto.class, auth);
    }

    public BookingDto[] getMyBookings(SessionAuth auth) {
        return get("/api/bookings/my", BookingDto[].class, auth);
    }
}
