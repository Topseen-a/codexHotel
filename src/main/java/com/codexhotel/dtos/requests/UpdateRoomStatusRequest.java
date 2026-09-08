package com.codexhotel.dtos.requests;

import com.codexhotel.data.enums.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoomStatusRequest {

    @NotBlank(message = "Room ID cannot be empty")
    private String roomId;

    @NotNull(message = "Room status cannot be empty")
    private RoomStatus roomStatus;
}
