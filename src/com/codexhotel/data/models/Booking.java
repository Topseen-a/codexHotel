package com.codexhotel.data.models;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.enums.RoomType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;
    private String userId;
    private String roomId;
    private RoomType roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private double totalPrice;
    private LocalDate createdAt;
}
