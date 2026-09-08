package com.codexhotel.dtos.responses;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.enums.RoomType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingResponse {

    private String bookingId;
    private String userId;
    private String roomId;
    private int roomNumber;
    private RoomType roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private double totalPrice;
}
