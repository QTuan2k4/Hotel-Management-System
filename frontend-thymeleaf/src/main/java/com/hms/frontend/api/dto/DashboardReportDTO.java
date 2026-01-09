package com.hms.frontend.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardReportDTO {
    private BigDecimal totalRevenue;
    private long totalBookings;
    private long totalRooms;
}
