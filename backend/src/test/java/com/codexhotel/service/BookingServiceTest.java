package com.codexhotel.service;

import com.codexhotel.enums.*;
import com.codexhotel.model.Booking;
import com.codexhotel.model.Pricing;
import com.codexhotel.model.Room;
import com.codexhotel.model.User;
import com.codexhotel.repository.BookingRepository;
import com.codexhotel.repository.PricingRepository;
import com.codexhotel.repository.RoomRepository;
import com.codexhotel.repository.UserRepository;
import com.codexhotel.dto.request.CancelBookingRequest;
import com.codexhotel.dto.request.CreateBookingRequest;
import com.codexhotel.dto.response.BookingResponse;
import com.codexhotel.exception.ForbiddenException;
import com.codexhotel.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PricingRepository pricingRepository;

    @BeforeEach
    public void setUp() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
        pricingRepository.deleteAll();
    }

    @Test
    public void testThatA_userCanBookA_room() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08012345678");
        user.setRole(Role.GUEST);

        User savedUser = userRepository.save(user);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setType(RoomType.STANDARD);
        room.setBasePrice(BigDecimal.valueOf(5_000));
        room.setStatus(RoomStatus.AVAILABLE);

        Room savedRoom = roomRepository.save(room);

        Pricing pricing = new Pricing();
        pricing.setRoomType(RoomType.STANDARD);
        pricing.setSeason(Season.WEEKDAY);
        pricing.setMultiplier(BigDecimal.valueOf(1));

        pricingRepository.save(pricing);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setUserId(savedUser.getId());
        request.setRoomType(savedRoom.getType());
        request.setCheckInDate(LocalDate.of(2027, 4, 6));
        request.setCheckOutDate(LocalDate.of(2027, 4, 8));

        BookingResponse response = bookingService.createBooking(request, savedUser.getId());

        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
    }

    @Test
    public void testThatBookingAnUnavailableRoomThrowsException() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08012345678");
        user.setRole(Role.GUEST);

        User savedUser = userRepository.save(user);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setType(RoomType.STANDARD);
        room.setBasePrice(BigDecimal.valueOf(5_000));
        room.setStatus(RoomStatus.AVAILABLE);

        Room savedRoom = roomRepository.save(room);

        Pricing pricing = new Pricing();
        pricing.setRoomType(RoomType.STANDARD);
        pricing.setSeason(Season.WEEKDAY);
        pricing.setMultiplier(BigDecimal.valueOf(1));

        pricingRepository.save(pricing);

        Booking existingBooking = new Booking();
        existingBooking.setUserId(savedUser.getId());
        existingBooking.setRoomId(savedRoom.getId());
        existingBooking.setCheckInDate(LocalDate.of(2027, 4, 10));
        existingBooking.setCheckOutDate(LocalDate.of(2027, 4, 15));
        existingBooking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(existingBooking);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setUserId(savedUser.getId());
        request.setRoomType(savedRoom.getType());
        request.setCheckInDate(LocalDate.of(2027, 4, 12));
        request.setCheckOutDate(LocalDate.of(2027, 4, 18));

        assertThrows(ConflictException.class, () -> bookingService.createBooking(request, savedUser.getId()));
    }

    @Test
    public void testThatBookingA_roomWithOverlappingDatesThrowsException() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08012345678");
        user.setRole(Role.GUEST);

        User savedUser = userRepository.save(user);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setType(RoomType.STANDARD);
        room.setBasePrice(BigDecimal.valueOf(5_000));
        room.setStatus(RoomStatus.AVAILABLE);

        Room savedRoom = roomRepository.save(room);

        Pricing pricing = new Pricing();
        pricing.setRoomType(RoomType.STANDARD);
        pricing.setSeason(Season.WEEKDAY);
        pricing.setMultiplier(BigDecimal.valueOf(1));

        pricingRepository.save(pricing);

        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setRoomId(savedRoom.getId());
        booking.setCheckInDate(LocalDate.of(2027, 4, 6));
        booking.setCheckOutDate(LocalDate.of(2027, 4, 10));
        booking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(booking);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setUserId(user.getId());
        request.setRoomType(savedRoom.getType());
        request.setCheckInDate(LocalDate.of(2027, 4, 8));
        request.setCheckOutDate(LocalDate.of(2027, 4, 12));

        assertThrows(ConflictException.class, () -> bookingService.createBooking(request, savedUser.getId()));
    }

    @Test
    public void testThatA_roomCanBeBookedFromTheDayAnotherBookingChecksOut() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08012345678");
        user.setRole(Role.GUEST);

        User savedUser = userRepository.save(user);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setType(RoomType.STANDARD);
        room.setBasePrice(BigDecimal.valueOf(5_000));
        room.setStatus(RoomStatus.AVAILABLE);

        Room savedRoom = roomRepository.save(room);

        for (Season season : Season.values()) {
            Pricing pricing = new Pricing();
            pricing.setRoomType(RoomType.STANDARD);
            pricing.setSeason(season);
            pricing.setMultiplier(BigDecimal.valueOf(1));
            pricingRepository.save(pricing);
        }

        Booking existingBooking = new Booking();
        existingBooking.setUserId(savedUser.getId());
        existingBooking.setRoomId(savedRoom.getId());
        existingBooking.setCheckInDate(LocalDate.of(2027, 4, 10));
        existingBooking.setCheckOutDate(LocalDate.of(2027, 4, 15));
        existingBooking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(existingBooking);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setUserId(savedUser.getId());
        request.setRoomType(RoomType.STANDARD);
        request.setCheckInDate(LocalDate.of(2027, 4, 15));
        request.setCheckOutDate(LocalDate.of(2027, 4, 17));

        BookingResponse response = bookingService.createBooking(request, savedUser.getId());

        assertEquals(savedRoom.getId(), response.getRoomId());
    }

    @Test
    public void testThatA_userCanCancelBookingRequest() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08012345678");
        user.setRole(Role.GUEST);

        User savedUser = userRepository.save(user);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setType(RoomType.STANDARD);
        room.setBasePrice(BigDecimal.valueOf(5_000));
        room.setStatus(RoomStatus.OCCUPIED);

        Room savedRoom = roomRepository.save(room);

        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setRoomId(room.getId());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        CancelBookingRequest request = new CancelBookingRequest();
        request.setBookingId(savedBooking.getId());

        BookingResponse response = bookingService.cancelBooking(request, user.getId());

        assertEquals(BookingStatus.CANCELLED, response.getStatus());
    }

    @Test
    public void testThatCancellingA_bookingDoesNotChangeTheRoomStatus() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08012345678");
        user.setRole(Role.GUEST);

        User savedUser = userRepository.save(user);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setType(RoomType.STANDARD);
        room.setBasePrice(BigDecimal.valueOf(5_000));
        room.setStatus(RoomStatus.MAINTENANCE);

        Room savedRoom = roomRepository.save(room);

        Booking booking = new Booking();
        booking.setUserId(savedUser.getId());
        booking.setRoomId(savedRoom.getId());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        CancelBookingRequest request = new CancelBookingRequest();
        request.setBookingId(savedBooking.getId());

        bookingService.cancelBooking(request, savedUser.getId());

        assertEquals(RoomStatus.MAINTENANCE, roomRepository.findById(savedRoom.getId()).orElseThrow().getStatus());
    }

    @Test
    public void testThatA_userCancelingAnotherUserBookingRequestThrowsException() {
        User userOne = new User();
        userOne.setName("Oluwaseun");
        userOne.setEmail("oluwaseun@gmail.com");
        userOne.setPhoneNumber("08012345678");
        userOne.setRole(Role.GUEST);

        User savedUserOne = userRepository.save(userOne);

        User userTwo = new User();
        userTwo.setName("Adedayo");
        userTwo.setEmail("adedayo@gmail.com");
        userTwo.setPhoneNumber("08149587216");
        userTwo.setRole(Role.GUEST);

        User savedUserTwo = userRepository.save(userTwo);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setType(RoomType.STANDARD);
        room.setBasePrice(BigDecimal.valueOf(5_000));
        room.setStatus(RoomStatus.OCCUPIED);

        Room savedRoom = roomRepository.save(room);

        Booking booking = new Booking();
        booking.setUserId(userOne.getId());
        booking.setRoomId(room.getId());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        CancelBookingRequest request = new CancelBookingRequest();
        request.setBookingId(savedBooking.getId());

        assertThrows(ForbiddenException.class, () -> bookingService.cancelBooking(request, userTwo.getId()));
    }

    @Test
    public void testThatUserCanGetBookingId() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08012345678");
        user.setRole(Role.GUEST);

        User savedUser = userRepository.save(user);

        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking saved = bookingRepository.save(booking);

        BookingResponse response = bookingService.getBookingById(saved.getId(), user.getId());

        assertEquals(saved.getId(), response.getBookingId());
    }

    @Test
    public void testThatA_userCanGetAnotherUserBookingIdThrowsException() {
        User userOne = new User();
        userOne.setName("Oluwaseun");
        userOne.setEmail("oluwaseun@gmail.com");
        userOne.setPhoneNumber("08012345678");
        userOne.setRole(Role.GUEST);

        User savedUserOne = userRepository.save(userOne);

        User userTwo = new User();
        userTwo.setName("Adedayo");
        userTwo.setEmail("adedayo@gmail.com");
        userTwo.setPhoneNumber("08149587216");
        userTwo.setRole(Role.GUEST);

        User savedUserTwo = userRepository.save(userTwo);

        Booking booking = new Booking();
        booking.setUserId(userOne.getId());

        Booking saved = bookingRepository.save(booking);

        assertThrows(ForbiddenException.class, () -> bookingService.getBookingById(saved.getId(), userTwo.getId()));
    }

    @Test
    public void testThatRoomsCanBeGottenByAdmin() {
        User admin = new User();
        admin.setName("Madam Bolu");
        admin.setEmail("bolu@gmail.com");
        admin.setPhoneNumber("08033297106");
        admin.setRole(Role.ADMIN);

        User savedAdmin = userRepository.save(admin);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setType(RoomType.STANDARD);
        room.setBasePrice(BigDecimal.valueOf(5_000));
        room.setStatus(RoomStatus.AVAILABLE);

        Room savedRoom = roomRepository.save(room);

        Booking booking = new Booking();
        booking.setUserId(savedAdmin.getId());
        booking.setRoomId(savedRoom.getId());
        booking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(booking);

        List<BookingResponse> result = bookingService.getBookingsByRoom(savedRoom.getId(), savedAdmin.getId());

        assertEquals(1, result.size());
    }

    @Test
    public void testThatGetBookingsByRoomForNonAdminThrowsException() {
        User user = new User();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPhoneNumber("08012345678");
        user.setRole(Role.GUEST);

        User savedUser = userRepository.save(user);

        assertThrows(ForbiddenException.class, () -> bookingService.getBookingsByRoom("roomId", user.getId()));
    }
}
