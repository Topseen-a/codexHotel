package com.codexhotel.dtos.requests;

import com.codexhotel.data.enums.RoomType;
import lombok.Data;

@Data
public class CreateRoomRequest {

    private int roomNumber;
    private RoomType roomType;
    private double basePrice;
}
