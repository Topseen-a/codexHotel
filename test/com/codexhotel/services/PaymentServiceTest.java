package com.codexhotel.services;

import com.codexhotel.data.enums.PaymentMethod;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
public class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        paymentRepository.deleteAll();
        bookingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testThatMakePaymentIsSuccessful() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08034567892");
        user.setRole(Role.GUEST);
        User savedUser = userRepository.save(user);

        Booking booking = new Booking();
        booking.setUserId(savedUser.getId());
        Booking savedBooking = bookingRepository.save(booking);

        PaymentRequest request = new PaymentRequest();
        request.setBookingId(savedBooking.getId());
        request.setAmount(5000);
        request.setPaymentMethod(PaymentMethod.valueOf("CARD"));

        PaymentResponse response = paymentService.makePayment(request);

        assertEquals(5000, response.getAmount());
    }

    @Test
    public void testThatMakePaymentBookingNotFoundThrowsException() {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId("123-456");
        request.setAmount(5000);
        request.setPaymentMethod(PaymentMethod.valueOf("CARD"));

        assertThrows(BookingNotFoundException.class, () -> paymentService.makePayment(request));
    }

    @Test
    public void testThatMakePaymentWithInvalidAmountThrowsException() {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId("123-456");
        request.setAmount(0);
        request.setPaymentMethod(PaymentMethod.valueOf("CARD"));

        assertThrows(AmountCannotBeLessThanZeroException.class, () -> paymentService.makePayment(request));
    }

    @Test
    public void testThatMakePaymentWithNoMethodThrowsException() {
        PaymentRequest request = new PaymentRequest();
        request.setBookingId("123-456");
        request.setAmount(5000);

        assertThrows(PaymentMethodCannotBeEmptyException.class, () -> paymentService.makePayment(request));
    }

    @Test
    public void testThatGetPaymentByIdByUserIsSuccessful() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwasen@gmail.com");
        user.setPhoneNumber("08022222222");
        user.setRole(Role.GUEST);
        User savedUser = userRepository.save(user);

        Payment payment = new Payment();
        payment.setUserId(savedUser.getId());
        payment.setSuccessful(true);
        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponse response = paymentService.getPaymentById(savedPayment.getId(), savedUser.getId());

        assertEquals(savedPayment.getId(), response.getPaymentId());
    }

    @Test
    public void testThatGetPaymentByIdByAdminIsSuccessful() {
        User admin = new User();
        admin.setName("Madam Bolu");
        admin.setEmail("bolu@gmail.com");
        admin.setPhoneNumber("08033333333");
        admin.setRole(Role.ADMIN);
        User savedAdmin = userRepository.save(admin);

        Payment payment = new Payment();
        payment.setUserId("123-456");
        payment.setSuccessful(true);
        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponse response = paymentService.getPaymentById(savedPayment.getId(), savedAdmin.getId());

        assertEquals(savedPayment.getId(), response.getPaymentId());
    }

    @Test
    public void testThatGetPaymentByIdForUnauthorizedUserThrowsException() {
        User userOne = new User();
        userOne.setName("Oluwaseun");
        userOne.setEmail("oluwaseun@gmail.com");
        userOne.setPhoneNumber("08011111111");
        userOne.setRole(Role.GUEST);

        userRepository.save(userOne);

        User userTwo = new User();
        userTwo.setName("Adedayo");
        userTwo.setEmail("dayo@gmail.com");
        userTwo.setPhoneNumber("08022222222");
        userTwo.setRole(Role.GUEST);

        userRepository.save(userTwo);

        Payment payment = new Payment();
        payment.setUserId(userOne.getId());
        Payment savedPayment = paymentRepository.save(payment);

        assertThrows(AdminAccessRequiredException.class, () -> paymentService.getPaymentById(savedPayment.getId(), userTwo.getId()));
    }

    @Test
    public void testThatGetPaymentsByBookingByUserIsSuccessful() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08034567892");
        user.setRole(Role.GUEST);

        userRepository.save(user);

        Booking booking = new Booking();
        booking.setUserId(user.getId());
        Booking savedBooking = bookingRepository.save(booking);

        Payment payment = new Payment();
        payment.setBookingId(savedBooking.getId());
        payment.setUserId(user.getId());
        paymentRepository.save(payment);

        List<PaymentResponse> result = paymentService.getPaymentsByBookingId(savedBooking.getId(), user.getId());

        assertEquals(1, result.size());
    }

    @Test
    public void testThatGetPaymentsByBookingForUnauthorizedUserThrowsException() {
        User userOne = new User();
        userOne.setName("Oluwaseun");
        userOne.setEmail("oluwaseun@gmail.com");
        userOne.setPhoneNumber("08011111111");
        userOne.setRole(Role.GUEST);

        userRepository.save(userOne);

        User userTwo = new User();
        userTwo.setName("Adedayo");
        userTwo.setEmail("dayo@gmail.com");
        userTwo.setPhoneNumber("08022222222");
        userTwo.setRole(Role.GUEST);

        userRepository.save(userTwo);

        Booking booking = new Booking();
        booking.setUserId(userOne.getId());
        Booking savedBooking = bookingRepository.save(booking);

        assertThrows(AdminAccessRequiredException.class, () -> paymentService.getPaymentsByBookingId(savedBooking.getId(), userTwo.getId()));
    }

    @Test
    public void testThatGetPaymentsByStatusByAdminIsSuccessful() {
        User admin = new User();
        admin.setRole(Role.ADMIN);
        User savedAdmin = userRepository.save(admin);

        Payment payment = new Payment();
        payment.setSuccessful(true);
        paymentRepository.save(payment);

        List<PaymentResponse> result = paymentService.getPaymentsByStatus(true, savedAdmin.getId());

        assertEquals(1, result.size());
    }

    @Test
    public void testThatGetPaymentsByStatusByNonAdminThrowsException() {
        User user = new User();
        user.setRole(Role.GUEST);
        User savedUser = userRepository.save(user);

        assertThrows(AdminAccessRequiredException.class, () -> paymentService.getPaymentsByStatus(true, savedUser.getId()));
    }

    @Test
    public void testThatMarkPaymentAsSuccessfulByAdminIsSuccessful() {
        User admin = new User();
        admin.setRole(Role.ADMIN);
        User savedAdmin = userRepository.save(admin);

        Payment payment = new Payment();
        payment.setSuccessful(false);
        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponse updated = paymentService.markPaymentAsSuccessful(savedPayment.getId(), savedAdmin.getId());

        assertTrue(updated.isSuccessful());
    }

    @Test
    public void testThatMarkPaymentAsSuccessfulByNonAdminThrowsException() {
        User user = new User();
        user.setRole(Role.GUEST);
        User savedUser = userRepository.save(user);

        assertThrows(AdminAccessRequiredException.class, () -> paymentService.markPaymentAsSuccessful("id", savedUser.getId()));
    }

    @Test
    public void testThatDeletePaymentByAdminIsSuccessful() {
        User admin = new User();
        admin.setRole(Role.ADMIN);
        User savedAdmin = userRepository.save(admin);

        Payment payment = paymentRepository.save(new Payment());

        paymentService.deletePaymentById(payment.getId(), savedAdmin.getId());

        assertEquals(0, paymentRepository.count());
    }

    @Test
    public void testThatDeletePaymentByNonAdminThrowsException() {
        User user = new User();
        user.setRole(Role.GUEST);
        User savedUser = userRepository.save(user);

        assertThrows(AdminAccessRequiredException.class, () -> paymentService.deletePaymentById("id", savedUser.getId()));
    }
}