package com.hms.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Target role: ADMIN, USER, or null for specific user
     */
    @Column(name = "recipient_role")
    private String recipientRole;

    /**
     * Specific user ID (for user-specific notifications)
     */
    @Column(name = "recipient_user_id")
    private Long recipientUserId;

    /**
     * Notification type: BOOKING_CREATED, PAYMENT_RECEIVED, BOOKING_APPROVED, etc.
     */
    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    /**
     * Reference ID (bookingId, paymentId, etc.)
     */
    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "is_read")
    private boolean isRead = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
