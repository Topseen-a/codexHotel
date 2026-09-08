package com.codexhotel.services;

import com.codexhotel.data.enums.BookingStatus;
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

    public PaymentResponse makePayment(PaymentRequest request, String requesterId) {
        validatePaymentRequest(request);

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!isStaff(requester) && !booking.getUserId().equals(requesterId)) {
            throw new UnauthorizedActionException("You are not allowed to pay for this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidPaymentAmountException("Cannot pay for a cancelled booking");
        }

        double alreadyPaid = paymentRepository.findByBookingId(booking.getId())
                .stream()
                .filter(Payment::isSuccessful)
                .mapToDouble(Payment::getAmount)
                .sum();

        double outstanding = booking.getTotalPrice() - alreadyPaid;

        if (request.getAmount() > outstanding + 0.01) {
            throw new InvalidPaymentAmountException(
                    "Payment amount exceeds the outstanding balance of N" + outstanding);
        }

        User bookingOwner = userRepository.findById(booking.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Payment payment = PaymentMapper.toPayment(request);
        payment.setSuccessful(true);
        payment.setUserId(bookingOwner.getId());

        Payment savedPayment = paymentRepository.save(payment);

        notificationManager.notifyByEmailAndSms(bookingOwner.getEmail(), bookingOwner.getPhoneNumber(),
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

        if (!isStaff(requester) && !payment.getUserId().equals(requesterId)) {
            throw new UnauthorizedActionException("You are not allowed to access this payment");
        }

        return PaymentMapper.toResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByBookingId(String bookingId, String requesterId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!isStaff(requester) && !booking.getUserId().equals(requesterId)) {
            throw new UnauthorizedActionException("You are not allowed to access these payments");
        }

        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    public List<PaymentResponse> getPaymentsByStatus(boolean successful, String requesterId) {
        requireStaff(requesterId, "Only staff can view payments by status");

        return paymentRepository.findBySuccessful(successful)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    public PaymentResponse markPaymentAsSuccessful(String paymentId, String requesterId) {
        requireStaff(requesterId, "Only staff can mark payments as successful");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        payment.setSuccessful(true);
        Payment updatedPayment = paymentRepository.save(payment);

        return PaymentMapper.toResponse(updatedPayment);
    }

    public void deletePaymentById(String paymentId, String requesterId) {
        // Deleting financial records is admin-only, unlike other staff payment actions.
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (requester.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only admins can delete payments");
        }
        if (!paymentRepository.existsById(paymentId)) {
            throw new PaymentNotFoundException("Payment not found");
        }

        paymentRepository.deleteById(paymentId);
    }

    private void requireStaff(String requesterId, String message) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!isStaff(requester)) {
            throw new UnauthorizedActionException(message);
        }
    }

    private boolean isStaff(User user) {
        return user.getRole() == Role.ADMIN
                || user.getRole() == Role.MANAGER
                || user.getRole() == Role.RECEPTIONIST;
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
