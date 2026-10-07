package com.codexhotel.dto.request;

import com.codexhotel.enums.RoomType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBookingRequest {

    @NotBlank(message = "User ID cannot be empty")
    private String userId;

    @NotNull(message = "Room type cannot be empty")
    private RoomType roomType;

    @NotNull(message = "Check-in date cannot be empty")
    @FutureOrPresent(message = "Check-in date cannot be in the past")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date cannot be empty")
    private LocalDate checkOutDate;
}
