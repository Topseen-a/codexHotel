package com.codexhotel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private int totalRooms;
    private int occupiedRooms;
    private int availableRooms;
    private long activeBookings;
    private BigDecimal totalRevenue;
    private double occupancyRate;
}
