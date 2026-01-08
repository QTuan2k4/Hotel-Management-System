package com.hms.booking.client;

import java.math.BigDecimal;

public class RoomSummary {
    private Long id;
    private BigDecimal pricePerNight;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
}
