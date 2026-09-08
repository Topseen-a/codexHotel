package com.codexhotel.mapper;

import com.codexhotel.data.models.Payment;
import com.codexhotel.dtos.requests.PaymentRequest;
import com.codexhotel.dtos.responses.PaymentResponse;

import java.time.LocalDate;

public class PaymentMapper {

    public static Payment toPayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(LocalDate.now());
        payment.setSuccessful(false);

        return payment;
    }

    public static PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setBookingId(payment.getBookingId());
        response.setAmount(payment.getAmount());
        response.setPaymentDate(payment.getPaymentDate());
        response.setSuccessful(payment.isSuccessful());

        return response;
    }
}
