package com.codexhotel.dto.request;

import com.codexhotel.enums.RoomStatus;
import com.codexhotel.enums.RoomType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateRoomRequest {

    @Min(value = 1, message = "Room number must be greater than 0")
    private int roomNumber;

    @NotNull(message = "Room type cannot be empty")
    private RoomType roomType;

    @NotNull(message = "Room status cannot be empty")
    private RoomStatus roomStatus;

    @NotNull(message = "Base price cannot be empty")
    @PositiveOrZero(message = "Base price cannot be negative")
    @Digits(integer = 12, fraction = 2, message = "Base price can have at most 2 decimal places")
    private BigDecimal basePrice;
}
