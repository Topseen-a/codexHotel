package com.codexhotel.dtos.responses;

import lombok.Data;

@Data
public class ReportResponse {

    private int totalRooms;
    private int occupiedRooms;
    private int availableRooms;
    private long activeBookings;
    private double totalRevenue;
    private double occupancyRate;
}
