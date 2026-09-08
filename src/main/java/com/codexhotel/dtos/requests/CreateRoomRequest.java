package com.codexhotel.dtos.requests;

import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.enums.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateRoomRequest {

    @Min(value = 1, message = "Room number must be greater than 0")
    private int roomNumber;

    @NotNull(message = "Room type cannot be empty")
    private RoomType roomType;

    @NotNull(message = "Room status cannot be empty")
    private RoomStatus roomStatus;

    @PositiveOrZero(message = "Base price cannot be negative")
    private double basePrice;
}
