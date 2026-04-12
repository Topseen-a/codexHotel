package com.codexhotel.dtos.responses;

import com.codexhotel.data.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingResponse {

    private String bookingId;
    private String userId;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private double totalPrice;
}
