package com.hms.notification.repository;

import com.hms.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications for admin (recipientRole = 'ADMIN')
     * Ordered by createdAt desc, limited
     */
    @Query("SELECT n FROM Notification n WHERE n.recipientRole = 'ADMIN' ORDER BY n.createdAt DESC")
    List<Notification> findAdminNotifications();

    /**
     * Find unread notifications for admin
     */
    @Query("SELECT n FROM Notification n WHERE n.recipientRole = 'ADMIN' AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadAdminNotifications();

    /**
     * Count unread notifications for admin
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipientRole = 'ADMIN' AND n.isRead = false")
    long countUnreadAdmin();

    /**
     * Find notifications for a specific user
     */
    @Query("SELECT n FROM Notification n WHERE n.recipientUserId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByRecipientUserId(@Param("userId") Long userId);

    /**
     * Count unread notifications for a specific user
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipientUserId = :userId AND n.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);
}
