package com.codexhotel.service.impl;

import com.codexhotel.dto.request.PaymentRequest;
import com.codexhotel.dto.response.PaymentResponse;
import com.codexhotel.enums.BookingStatus;
import com.codexhotel.enums.Role;
import com.codexhotel.exception.BadRequestException;
import com.codexhotel.exception.ResourceNotFoundException;
import com.codexhotel.mapper.PaymentMapper;
import com.codexhotel.model.Booking;
import com.codexhotel.model.Payment;
import com.codexhotel.model.User;
import com.codexhotel.notification.NotificationManager;
import com.codexhotel.repository.BookingRepository;
import com.codexhotel.repository.PaymentRepository;
import com.codexhotel.security.AccessGuard;
import com.codexhotel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final AccessGuard accessGuard;
    private final NotificationManager notificationManager;

    @Override
    public PaymentResponse makePayment(PaymentRequest request, String requesterId) {
        Booking booking = findBooking(request.getBookingId());

        accessGuard.requireOwnerOrStaff(requesterId, booking.getUserId(), "You are not allowed to pay for this booking");

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot pay for a cancelled booking");
        }

        BigDecimal alreadyPaid = paymentRepository.findByBookingId(booking.getId())
                .stream()
                .filter(Payment::isSuccessful)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal outstanding = booking.getTotalPrice().subtract(alreadyPaid);

        if (request.getAmount().compareTo(outstanding) > 0) {
            throw new BadRequestException("Payment amount exceeds the outstanding balance of N" + outstanding);
        }

        User bookingOwner = accessGuard.requireUser(booking.getUserId());

        Payment payment = PaymentMapper.toPayment(request);
        payment.setUserId(bookingOwner.getId());
        payment.setPaymentDate(LocalDate.now());
        payment.setSuccessful(true);

        Payment savedPayment = paymentRepository.save(payment);

        notificationManager.notifyByEmailAndSms(bookingOwner.getEmail(), bookingOwner.getPhoneNumber(),
                "Your payment of N" + payment.getAmount() +
                        " for booking " + booking.getId() +
                        " has been received successfully.");

        return PaymentMapper.toResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentById(String paymentId, String requesterId) {
        Payment payment = findPayment(paymentId);

        accessGuard.requireOwnerOrStaff(requesterId, payment.getUserId(), "You are not allowed to access this payment");

        return PaymentMapper.toResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByBookingId(String bookingId, String requesterId) {
        Booking booking = findBooking(bookingId);

        accessGuard.requireOwnerOrStaff(requesterId, booking.getUserId(), "You are not allowed to access these payments");

        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    @Override
    public List<PaymentResponse> getPaymentsByStatus(boolean successful, String requesterId) {
        accessGuard.requireStaff(requesterId, "Only staff can view payments by status");

        return paymentRepository.findBySuccessful(successful)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse markPaymentAsSuccessful(String paymentId, String requesterId) {
        accessGuard.requireStaff(requesterId, "Only staff can mark payments as successful");

        Payment payment = findPayment(paymentId);
        payment.setSuccessful(true);

        return PaymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Override
    public void deletePaymentById(String paymentId, String requesterId) {
        accessGuard.requireRole(requesterId, "Only admins can delete payments", Role.ADMIN);

        paymentRepository.delete(findPayment(paymentId));
    }

    private Booking findBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private Payment findPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }
}
