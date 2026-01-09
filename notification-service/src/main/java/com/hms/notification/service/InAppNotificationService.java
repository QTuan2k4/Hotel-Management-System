package com.hms.notification.service;

import com.hms.notification.entity.Notification;
import com.hms.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class InAppNotificationService {

    private final NotificationRepository repository;

    public InAppNotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    /**
     * Create notification for admin
     */
    @Transactional
    public Notification notifyAdmin(String type, String title, String message, Long referenceId) {
        Notification notification = Notification.builder()
                .recipientRole("ADMIN")
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .isRead(false)
                .build();
        return repository.save(notification);
    }

    /**
     * Create notification for specific user
     */
    @Transactional
    public Notification notifyUser(Long userId, String type, String title, String message, Long referenceId) {
        Notification notification = Notification.builder()
                .recipientUserId(userId)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .isRead(false)
                .build();
        return repository.save(notification);
    }

    /**
     * Get recent notifications for admin (last 20)
     */
    public List<Notification> getAdminNotifications() {
        List<Notification> all = repository.findAdminNotifications();
        return all.size() > 20 ? all.subList(0, 20) : all;
    }

    /**
     * Get unread count for admin
     */
    public long getUnreadCountAdmin() {
        return repository.countUnreadAdmin();
    }

    /**
     * Get notifications for user
     */
    public List<Notification> getUserNotifications(Long userId) {
        List<Notification> all = repository.findByRecipientUserId(userId);
        return all.size() > 20 ? all.subList(0, 20) : all;
    }

    /**
     * Get unread count for user
     */
    public long getUnreadCountUser(Long userId) {
        return repository.countUnreadByUserId(userId);
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        repository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            repository.save(n);
        });
    }

    /**
     * Mark all admin notifications as read
     */
    @Transactional
    public void markAllAdminAsRead() {
        List<Notification> unread = repository.findUnreadAdminNotifications();
        unread.forEach(n -> n.setRead(true));
        repository.saveAll(unread);
    }

    /**
     * Mark all user notifications as read
     */
    @Transactional
    public void markAllUserAsRead(Long userId) {
        List<Notification> notifications = repository.findByRecipientUserId(userId);
        notifications.stream().filter(n -> !n.isRead()).forEach(n -> n.setRead(true));
        repository.saveAll(notifications);
    }
}
