package com.codexhotel.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelBookingRequest {

    @NotBlank(message = "Booking Id cannot be empty")
    private String bookingId;
}
