package com.codexhotel.dtos.requests;

import com.codexhotel.data.enums.RoomType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBookingRequest {

    private String userId;
    private RoomType RoomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
