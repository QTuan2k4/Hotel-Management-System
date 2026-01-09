# In-App Notification System

## 📋 Description

Implements a complete in-app notification system for the Hotel Management System using a Polling + Database approach. This feature allows real-time notifications for both admin and regular users.

## ✨ Features

### For Admin:
- 🔔 Receive notifications when users create new bookings
- 🔔 Notification bell with unread count badge in navbar
- Click notifications to navigate to bookings page
- Mark individual or all notifications as read

### For Users:
- 🔔 Receive notifications when admin performs check-in
- 🔔 Receive notifications when admin performs check-out
- 🔔 Receive notifications when admin cancels booking
- Click notifications to navigate to bookings page

## 🏗️ Technical Changes

### New Files Created:

| Service | File | Description |
|---------|------|-------------|
| notification-service | `Notification.java` | JPA Entity for notifications |
| notification-service | `NotificationRepository.java` | Repository with custom queries |
| notification-service | `InAppNotificationService.java` | Service for notification operations |
| notification-service | `CreateNotificationRequest.java` | DTO for creating notifications |
| notification-service | `NotificationDto.java` | DTO for API responses |
| booking-service | `InternalBookingController.java` | Internal API (unused, can be removed) |
| billing-service | `BookingClient.java` | Client for booking-service (unused, can be removed) |
| frontend-thymeleaf | `NotificationProxyController.java` | Proxy for frontend JS to call notification API |
| frontend-thymeleaf | `scripts.html` | Notification polling JavaScript fragment |

### Modified Files:

| Service | File | Changes |
|---------|------|---------|
| notification-service | `pom.xml` | Added JPA, MySQL, Lombok dependencies |
| notification-service | `application.yml` | Added MySQL datasource config |
| notification-service | `NotificationController.java` | Added endpoints for admin/user notifications |
| booking-service | `NotificationClient.java` | Added in-app notification methods |
| booking-service | `BookingAppService.java` | Send notification on booking creation |
| booking-service | `AdminBookingService.java` | Send notifications on check-in/out/cancel |
| api-gateway | `JwtAuthFilter.java` | Allow internal key auth for notifications |
| frontend-thymeleaf | `nav.html` | Added notification bell for all users |
| frontend-thymeleaf | `AdminDashboardController.java` | Pass auth to model |
| frontend-thymeleaf | `AdminBookingWebController.java` | Pass auth to model |
| frontend-thymeleaf | `AdminRoomController.java` | Pass auth to model |
| frontend-thymeleaf | `UserDashboardController.java` | Pass auth to model |
| frontend-thymeleaf | Admin templates | Include notification script fragment |
| frontend-thymeleaf | `user/dashboard.html` | Include notification script fragment |

## 📡 API Endpoints

### Notification Service:
- `POST /api/notifications` - Create notification (internal use)
- `GET /api/notifications/admin` - Get admin notifications
- `GET /api/notifications/user/{userId}` - Get user notifications
- `POST /api/notifications/{id}/read` - Mark as read
- `POST /api/notifications/admin/read-all` - Mark all admin notifications as read
- `POST /api/notifications/user/{userId}/read-all` - Mark all user notifications as read

### Frontend Proxy:
- `GET /api/notify/admin` - Proxy for admin notifications
- `GET /api/notify/user` - Proxy for user notifications
- `POST /api/notify/{id}/read` - Proxy to mark as read
- `POST /api/notify/admin/read-all` - Proxy to mark all admin as read
- `POST /api/notify/user/read-all` - Proxy to mark all user as read

## 🗄️ Database

New table `notifications` in `hms_notification` database:
- `id` - Primary key
- `recipient_role` - "ADMIN" for admin notifications
- `recipient_user_id` - User ID for user-specific notifications
- `type` - BOOKING_CREATED, CHECKED_IN, CHECKED_OUT, BOOKING_CANCELLED
- `title` - Notification title
- `message` - Notification content
- `reference_id` - Related booking ID
- `is_read` - Read status
- `created_at` - Creation timestamp

## 🔧 Configuration Required

Ensure `hms_notification` database exists:
```sql
CREATE DATABASE IF NOT EXISTS hms_notification;
```

## 🧪 Testing

1. Start all services
2. Login as user → Create booking → Admin receives notification
3. Login as admin → Check-in booking → User receives notification
4. Login as admin → Check-out booking → User receives notification
5. Login as admin → Cancel booking → User receives notification
6. Click notification → Navigate to relevant page
7. Mark all as read → Badge disappears

## 📸 Screenshots

*(Add screenshots of notification bell, dropdown, etc.)*

## ⚠️ Notes

- Polling interval: 30 seconds
- Max notifications displayed: 10 (most recent)
- Unused files can be cleaned up: `InternalBookingController.java`, `BookingClient.java` in billing-service
