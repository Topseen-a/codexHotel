package com.codexhotel.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelBookingRequest {

    @NotBlank(message = "Booking Id cannot be empty")
    private String bookingId;
}
