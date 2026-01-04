package com.hms.common.dto.booking;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateBookingRequest {
    @NotNull private Long roomId;
    @NotNull private LocalDate checkInDate;
    @NotNull private LocalDate checkOutDate;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
}
