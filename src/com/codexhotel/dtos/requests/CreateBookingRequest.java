package com.codexhotel.dtos.requests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBookingRequest {

    private String userId;
    private String roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
