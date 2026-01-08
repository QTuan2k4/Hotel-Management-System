package com.hms.common.dto.booking;

public enum BookingStatus {
    PENDING_APPROVAL,
    APPROVED,
    CONFIRMED, // Auto-confirmed booking (no admin approval needed)
    REJECTED,
    CANCELLED,
    CHECKED_IN,
    CHECKED_OUT,
    NO_SHOW
}
