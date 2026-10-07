package com.codexhotel.mapper;

import com.codexhotel.model.Payment;
import com.codexhotel.dto.request.PaymentRequest;
import com.codexhotel.dto.response.PaymentResponse;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static Payment toPayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        return payment;
    }

    public static PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .successful(payment.isSuccessful())
                .build();
    }
}
