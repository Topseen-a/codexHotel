package com.codexhotel.dtos.requests;

import com.codexhotel.data.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotBlank(message = "Booking Id cannot be empty")
    private String bookingId;

    @Positive(message = "Amount must be greater than 0")
    private double amount;

    @NotNull(message = "Payment method cannot be empty")
    private PaymentMethod paymentMethod;
}
