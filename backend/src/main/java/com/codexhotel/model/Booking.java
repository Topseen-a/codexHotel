package com.codexhotel.model;

import com.codexhotel.enums.BookingStatus;
import com.codexhotel.enums.RoomType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
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

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalPrice;

    private LocalDate createdAt;
}
