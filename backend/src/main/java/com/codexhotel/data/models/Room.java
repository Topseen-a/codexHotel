package com.codexhotel.data.models;

import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.enums.RoomType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "rooms")
public class Room {

    @Id
    private String id;
    private int roomNumber;
    private RoomType type;
    private double basePrice;
    private RoomStatus status;
}
