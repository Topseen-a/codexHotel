package com.codexhotel.dtos.requests;

import com.codexhotel.data.enums.RoomStatus;
import lombok.Data;

@Data
public class UpdateRoomStatusRequest {

    private String roomId;
    private RoomStatus roomStatus;
}
