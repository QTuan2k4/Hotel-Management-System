# Pull Request: Implement Report Service & Dashboard Integration

## 📝 Description
This PR implements the **Report Service** and integrates it into the **Admin Dashboard** to display real-time statistics (Total Revenue, Bookings, Rooms).

## 🚀 Changes Implemented

### 1. New Service: Report Service (`report-service`)
*   **Architecture:** Aggregator pattern using Feign Clients.
*   **Clients:** 
    *   `BookingClient`: Fetches booking counts (calls `booking-service`).
    *   `BillingClient`: Fetches total revenue (calls `billing-service`).
    *   `RoomClient`: Fetches room counts (calls `room-service`).
*   **API:** `GET /api/reports/dashboard` returns aggregated stats.

### 2. Backend Updates (Microservices)
*   **Booking Service:** Added internal API `GET /internal/bookings/stats/count`.
*   **Billing Service:** 
    *   Added `sumTotalPaid()` query in Repository.
    *   Added internal API `GET /api/internal/invoices/stats/revenue`.
*   **Room Service:** Added `InternalRoomController` with API `GET /internal/rooms/stats/count`.

### 3. Frontend Integration (`frontend-thymeleaf`)
*   **UI:** Added statistics cards (Revenue, Bookings, Rooms) to the Admin Dashboard top row.
*   **Logic:** `AdminDashboardController` now calls the Report Service via Gateway to fetch and display the data.

## ✅ Verification Steps
1.  Start all services (Discovery, Auth, Gateway, Services, Frontend).
2.  Login as **Admin**.
3.  Navigate to **/admin**.
4.  Verify that the top cards show numbers greater than or equal to 0 (not empty).

## 📸 Screenshots
(Add screenshots of the new Dashboard here if applicable)
