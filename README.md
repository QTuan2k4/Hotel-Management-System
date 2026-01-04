# Hotel SOA (Phase 0-1-2) - Spring Boot + Thymeleaf + MySQL (XAMPP)

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
- booking-service: 9002 (skeleton)
- billing-service: 9003 (skeleton)
- payment-service: 9004 (skeleton)
- report-service: 9005 (skeleton)
- notification-service: 9006 (skeleton)

## Lưu ý quan trọng về JWT secret
`api-gateway` và `auth-service` phải dùng **cùng** `security.jwt.secret` (HS256).
Hiện tại đang để: `CHANGE_ME_SUPER_SECRET_KEY_32_BYTES_MINIMUM____`
-> Bạn có thể đổi, nhưng nhớ đổi ở **cả 2 service**.

## Tài khoản admin mặc định
- username: `admin`
- password: `admin123`

## Chạy project (không cần cài mvn)
1) Mở XAMPP -> Start MySQL
2) Mở terminal tại thư mục `hotel-soa/`
3) Chạy theo thứ tự (mỗi lệnh 1 terminal riêng):

Windows:
- `mvnw.cmd -pl auth-service spring-boot:run`
- `mvnw.cmd -pl room-service spring-boot:run`
- `mvnw.cmd -pl api-gateway spring-boot:run`
- `mvnw.cmd -pl frontend-thymeleaf spring-boot:run`

Mac/Linux:
- `chmod +x mvnw`
- `./mvnw -pl auth-service spring-boot:run`
- `./mvnw -pl room-service spring-boot:run`
- `./mvnw -pl api-gateway spring-boot:run`
- `./mvnw -pl frontend-thymeleaf spring-boot:run`

## Truy cập UI
- Home list rooms: http://localhost:8080/
- Login: http://localhost:8080/login
- Admin rooms: http://localhost:8080/admin/rooms (cần login admin)

## Test nhanh API qua Gateway
- Public rooms: `GET http://localhost:8081/api/rooms`
- Login (lấy token):
  `POST http://localhost:8081/api/auth/login`
  body JSON:
  `{ "username": "admin", "password": "admin123" }`

- Admin create room (qua gateway):
  `POST http://localhost:8081/api/admin/rooms`
  Header: `Authorization: Bearer <token>`
  Body:
  `{ "code":"R999", "name":"Phòng test", "type":"STANDARD", "pricePerNight":500000, "status":"AVAILABLE", "description":"..." }`

## Phase đã làm
- Phase 0: skeleton multi-module + config port + schema MySQL cho từng service
- Phase 1: Auth + JWT + Gateway routing + validate JWT + logging
- Phase 2: Room CRUD (admin) + UI list & detail + UI admin rooms

## Phase sau (đã tạo sẵn skeleton)
booking-service, billing-service, payment-service, report-service, notification-service đã có project và cấu hình DB/port.
Bạn chỉ cần implement tiếp theo kế hoạch (Phase 3+).
