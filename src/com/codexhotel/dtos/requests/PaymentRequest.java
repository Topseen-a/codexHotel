package com.codexhotel.dtos.requests;

import com.codexhotel.data.enums.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequest {

    private String bookingId;
    private double amount;
    private PaymentMethod paymentMethod;
}
