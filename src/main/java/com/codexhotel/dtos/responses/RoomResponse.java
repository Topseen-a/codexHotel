package com.codexhotel.dtos.responses;

import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.enums.RoomType;
import lombok.Data;

@Data
public class RoomResponse {

    private String id;
    private int roomNumber;
    private RoomType type;
    private double basePrice;
    private RoomStatus status;
}
