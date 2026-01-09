package com.hms.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    private String recipientRole;  // ADMIN or USER
    private Long recipientUserId;  // For user-specific notifications
    private String type;           // BOOKING_CREATED, PAYMENT_RECEIVED, etc.
    private String title;
    private String message;
    private Long referenceId;      // bookingId, paymentId, etc.
}
