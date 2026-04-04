package com.codexhotel.dtos.responses;

import lombok.Data;

@Data
public class ReportResponse {

    private int totalRooms;
    private int occupiedRooms;
    private double totalRevenue;
}
