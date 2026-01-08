# Hotel SOA (Phase 0-1-5) - Spring Boot + Thymeleaf + MySQL (XAMPP)

## Yêu cầu môi trường
- JDK 21
- XAMPP MySQL chạy port **3306**
- Username **root**, password **rỗng**
- Internet để Maven download dependency lần đầu

## Ports
- frontend-thymeleaf: 8080
- api-gateway: 8081
- auth-service: 9000
- room-service: 9001
- booking-service: 9002
- billing-service: 9003
- payment-service: 9004
- report-service: 9005 (skeleton)
- notification-service: 9006 (skeleton)

## MySQL schemas (mỗi service 1 schema)
- hms_auth_db
- hms_room_db
- hms_booking_db
- hms_billing_db
- hms_payment_db
- hms_report_db
- hms_notification_db

Rule: mỗi service chỉ truy cập schema của nó, không join cross-schema.

---

## Lưu ý quan trọng về JWT secret
`api-gateway` và `auth-service` phải dùng **cùng** `security.jwt.secret` (HS256).
Hiện tại đang để:
`CHANGE_ME_SUPER_SECRET_KEY_32_BYTES_MINIMUM____`

-> Bạn có thể đổi, nhưng nhớ đổi ở **cả 2 service**.

## Internal Key (Phase 5 - gọi nội bộ giữa services)
Gateway bảo vệ `/api/internal/**` bằng header:

- Header: `X-Internal-Key: HMS_INTERNAL_2025`

Key cấu hình tại `api-gateway`:
- `gateway.security.internalKey = HMS_INTERNAL_2025`

Booking/Payment gọi Billing qua gateway bằng `X-Internal-Key` này.

---

## Tài khoản admin mặc định
- username: `admin`
- password: `admin123`

---

# Chạy project (không cần cài mvn)
## 1) Start MySQL
Mở XAMPP -> Start MySQL (port 3306)

## 2) Chạy theo thứ tự (mỗi lệnh 1 terminal riêng)
### Windows
- `mvnw.cmd -pl auth-service spring-boot:run`
- `mvnw.cmd -pl room-service spring-boot:run`
- `mvnw.cmd -pl booking-service spring-boot:run`
- `mvnw.cmd -pl billing-service spring-boot:run`
- `mvnw.cmd -pl payment-service spring-boot:run`
- `mvnw.cmd -pl api-gateway spring-boot:run`
- `mvnw.cmd -pl frontend-thymeleaf spring-boot:run`
---

# Truy cập UI
- Home list rooms: http://localhost:8080/
- Login: http://localhost:8080/login
- Register: http://localhost:8080/register
- My bookings: http://localhost:8080/bookings/my
- My invoices: http://localhost:8080/invoices/my
- Admin dashboard: http://localhost:8080/admin
- Admin rooms: http://localhost:8080/admin/rooms
- Admin bookings: http://localhost:8080/admin/bookings
- Admin invoices: http://localhost:8080/admin/invoices

---

# Test nhanh API qua Gateway

## Public
- Rooms: `GET http://localhost:8081/api/rooms`
- Room detail: `GET http://localhost:8081/api/rooms/{id}`

## Login (lấy token)
`POST http://localhost:8081/api/auth/login`
Body JSON:
```json
{ "username": "admin", "password": "admin123" }
