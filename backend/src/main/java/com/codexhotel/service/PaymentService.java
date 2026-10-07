package com.codexhotel.service;

import com.codexhotel.dto.request.PaymentRequest;
import com.codexhotel.dto.response.PaymentResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface PaymentService {

    PaymentResponse makePayment(@Valid PaymentRequest request, String requesterId);

    PaymentResponse getPaymentById(String paymentId, String requesterId);

    List<PaymentResponse> getPaymentsByBookingId(String bookingId, String requesterId);

    List<PaymentResponse> getPaymentsByStatus(boolean successful, String requesterId);

    PaymentResponse markPaymentAsSuccessful(String paymentId, String requesterId);

    void deletePaymentById(String paymentId, String requesterId);
}
