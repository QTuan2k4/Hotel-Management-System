package com.hms.report.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardReport {
    private BigDecimal totalRevenue;
    private long totalBookings;
    private long totalRooms;
    // Add more fields as needed: e.g. bookingsToday, occupiedRooms, etc.
}
