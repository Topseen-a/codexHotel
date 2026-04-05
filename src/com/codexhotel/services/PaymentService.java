package com.codexhotel.services;

import com.codexhotel.data.enums.Role;
import com.codexhotel.data.models.Booking;
import com.codexhotel.data.models.Payment;
import com.codexhotel.data.models.User;
import com.codexhotel.data.repositories.BookingRepository;
import com.codexhotel.data.repositories.PaymentRepository;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.requests.PaymentRequest;
import com.codexhotel.dtos.responses.PaymentResponse;
import com.codexhotel.exceptions.*;
import com.codexhotel.mapper.PaymentMapper;
import com.codexhotel.notifications.NotificationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NotificationManager notificationManager;

    public PaymentResponse createPayment(PaymentRequest request) {
        validatePaymentRequest(request);

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        User user = userRepository.findById(booking.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Payment payment = PaymentMapper.toPayment(request);
        payment.setSuccessful(true);
        payment.setUserId(user.getId());

        Payment savedPayment = paymentRepository.save(payment);


        notificationManager.notifyByEmailAndSms(user.getEmail(), user.getPhoneNumber(),
                "Your payment of N" + payment.getAmount() +
                        " for booking " + booking.getId() +
                        " has been received successfully.");

        return PaymentMapper.toResponse(savedPayment);
    }

    public PaymentResponse getPaymentById(String paymentId, String requesterId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!requester.getRole().equals(Role.ADMIN) && !payment.getUserId().equals(requesterId)) {
            throw new AdminAccessRequiredException("You are not allowed to access this payment");
        }

        return PaymentMapper.toResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByBookingId(String bookingId, String requesterId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!requester.getRole().equals(Role.ADMIN) && !booking.getUserId().equals(requesterId)) {
            throw new AdminAccessRequiredException("You are not allowed to access these payments");
        }

        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    public List<PaymentResponse> getPaymentsByStatus(boolean successful, String requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!requester.getRole().equals(Role.ADMIN)) {
            throw new AdminAccessRequiredException("Only admins can view payments by status");
        }

        return paymentRepository.findBySuccessful(successful)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    public PaymentResponse markPaymentAsSuccessful(String paymentId, String requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!requester.getRole().equals(Role.ADMIN)) {
            throw new AdminAccessRequiredException("Only admins can mark payments as successful");
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        payment.setSuccessful(true);
        Payment updatedPayment = paymentRepository.save(payment);

        return PaymentMapper.toResponse(updatedPayment);
    }

    public void deletePaymentById(String paymentId, String requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!requester.getRole().equals(Role.ADMIN)) {
            throw new AdminAccessRequiredException("Only admins can delete payments");
        }
        if (!paymentRepository.existsById(paymentId)) {
            throw new PaymentNotFoundException("Payment not found");
        }

        paymentRepository.deleteById(paymentId);
    }

    private void validatePaymentRequest(PaymentRequest request) {
        if (request.getBookingId() == null || request.getBookingId().trim().isEmpty()) {
            throw new BookingIdCannotBeEmptyException("Booking Id cannot be empty");
        }
        if (request.getAmount() <= 0) {
            throw new AmountCannotBeLessThanZeroException("Amount must be greater than 0");
        }
        if (request.getPaymentMethod() == null) {
            throw new PaymentMethodCannotBeEmptyException("Payment method cannot be empty");
        }
    }
}