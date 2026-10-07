package com.codexhotel.dtos.responses;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentResponse {

    private String paymentId;
    private String bookingId;
    private double amount;
    private LocalDate paymentDate;
    private boolean successful;
}
