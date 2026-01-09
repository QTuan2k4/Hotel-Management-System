package com.hms.report.service;

import com.hms.report.client.BillingClient;
import com.hms.report.client.BookingClient;
import com.hms.report.client.RoomClient;
import com.hms.report.dto.DashboardReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BookingClient bookingClient;
    private final BillingClient billingClient;
    private final RoomClient roomClient;

    public DashboardReport getDashboardReport() {
        long totalBookings = bookingClient.countTotalBookings();
        BigDecimal revenue = billingClient.getTotalRevenue();
        long totalRooms = roomClient.countTotalRooms();

        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }

        return DashboardReport.builder()
                .totalBookings(totalBookings)
                .totalRevenue(revenue)
                .totalRooms(totalRooms)
                .build();
    }

    public DashboardReport getDashboardReportByDate(int year, Integer month) {
        long bookingsInPeriod = bookingClient.countByDate(year, month);
        BigDecimal revenueInPeriod = billingClient.getRevenueByDate(year, month);
        long totalRooms = roomClient.countTotalRooms();

        if (revenueInPeriod == null) {
            revenueInPeriod = BigDecimal.ZERO;
        }

        return DashboardReport.builder()
                .totalBookings(bookingsInPeriod)
                .totalRevenue(revenueInPeriod)
                .totalRooms(totalRooms)
                .year(year)
                .month(month)
                .build();
    }
}
