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
    private Integer year;
    private Integer month;
}
