package com.codexhotel.dto.request;

import com.codexhotel.enums.PaymentMethod;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotBlank(message = "Booking Id cannot be empty")
    private String bookingId;

    @NotNull(message = "Amount cannot be empty")
    @Positive(message = "Amount must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Amount can have at most 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Payment method cannot be empty")
    private PaymentMethod paymentMethod;
}
