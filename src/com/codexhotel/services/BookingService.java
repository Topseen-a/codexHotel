package com.codexhotel.services;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.models.Booking;
import com.codexhotel.data.models.Room;
import com.codexhotel.data.models.User;
import com.codexhotel.data.repositories.BookingRepository;
import com.codexhotel.data.repositories.RoomRepository;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.requests.CancelBookingRequest;
import com.codexhotel.dtos.requests.CreateBookingRequest;
import com.codexhotel.dtos.responses.BookingResponse;
import com.codexhotel.exceptions.*;
import com.codexhotel.mapper.BookingMapper;
import com.codexhotel.notifications.NotificationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PricingService pricingService;
    private final UserRepository userRepository;
    private final NotificationManager notificationManager;

    public BookingResponse createBooking(CreateBookingRequest request) {
        validateBookingRequest(request);

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new RoomNotAvailableException("Room is not available");
        }

        validateRoomAvailability(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());

        Booking booking = BookingMapper.toBooking(request);

        double totalPrice = calculateTotalPrice(room, request.getCheckInDate(), request.getCheckOutDate());

        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        notificationManager.notifyByEmailAndSms(user.getEmail(), user.getPhoneNumber(),
                "Your booking for room " + room.getRoomNumber() +
                " from " + booking.getCheckInDate() + " to " + booking.getCheckOutDate() +
                " has been CONFIRMED. Total price: N" + totalPrice);

        return BookingMapper.toResponse(savedBooking);
    }

    public BookingResponse cancelBooking(CancelBookingRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);

        Room room = roomRepository.findById(booking.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        Booking updatedBooking = bookingRepository.save(booking);

        User user = userRepository.findById(booking.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        notificationManager.notifyByEmailAndSms(user.getEmail(), user.getPhoneNumber(),
                "Your booking for room " + room.getRoomNumber() +
                " from " + booking.getCheckInDate() + " to " + booking.getCheckOutDate() +
                " has been CANCELLED.");

        return BookingMapper.toResponse(updatedBooking);
    }

    public BookingResponse getBookingById(String id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        return BookingMapper.toResponse(booking);
    }

    public List<BookingResponse> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    public List<BookingResponse> getBookingsByRoom(String roomId) {
        return bookingRepository.findByRoomId(roomId)
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    public List<BookingResponse> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status)
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    private void validateBookingRequest(CreateBookingRequest request) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new UserIdCannotBeEmptyException("User ID cannot be empty");
        }
        if (request.getRoomId() == null || request.getRoomId().isBlank()) {
            throw new RoomIdCannotBeEmptyException("Room ID cannot be empty");
        }
        if (request.getCheckInDate() == null || request.getCheckOutDate() == null) {
            throw new DatesCannotBeEmptyException("Dates cannot be null");
        }
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new CheckOutAfterCheckInException("Check-out must be after check-in");
        }
    }

    private void validateRoomAvailability(String roomId, LocalDate checkIn, LocalDate checkOut) {
        List<Booking> bookings = bookingRepository.findByRoomId(roomId);

        for (Booking existing : bookings) {
            if (existing.getStatus() == BookingStatus.CANCELLED) continue;

            boolean overlaps = !(checkOut.isBefore(existing.getCheckInDate()) || checkIn.isAfter(existing.getCheckOutDate()));

            if (overlaps) {
                throw new RoomNotAvailableException("Room already booked for selected dates");
            }
        }
    }

    private double calculateTotalPrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        double total = 0;

        LocalDate currentDate = checkIn;

        while (currentDate.isBefore(checkOut)) {
            total += pricingService.calculatePrice(room.getType(), room.getBasePrice(), currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return total;
    }
}
