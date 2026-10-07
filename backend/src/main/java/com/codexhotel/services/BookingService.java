package com.codexhotel.services;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.enums.Role;
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
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PricingService pricingService;
    private final UserRepository userRepository;
    private final NotificationManager notificationManager;

    public BookingResponse createBooking(CreateBookingRequest request, String requesterId) {
        validateBookingRequest(request);

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Guests may only book for themselves; staff can book on behalf of any guest.
        if (!isStaff(requester) && !requester.getId().equals(request.getUserId())) {
            throw new UnauthorizedActionException("You can only create bookings for yourself");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Room> rooms = roomRepository.findByType(request.getRoomType());

        if (rooms.isEmpty()) {
            throw new RoomNotFoundException("No rooms found for this type");
        }

        Room room = rooms.stream()
                .filter(r -> r.getStatus() != RoomStatus.MAINTENANCE)
                .filter(r -> isRoomFree(r.getId(), request.getCheckInDate(), request.getCheckOutDate()))
                .findFirst()
                .orElseThrow(() -> new RoomNotAvailableException("No available room for selected dates"));

        Booking booking = BookingMapper.toBooking(request);

        booking.setRoomId(room.getId());
        booking.setTotalPrice(calculateTotalPrice(room, request.getCheckInDate(), request.getCheckOutDate()));
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        notificationManager.notifyByEmailAndSms(user.getEmail(), user.getPhoneNumber(),
                "Your booking for room " + room.getRoomNumber() +
                        " from " + booking.getCheckInDate() + " to " + booking.getCheckOutDate() +
                        " has been CONFIRMED. Total price: N" + booking.getTotalPrice());

        return BookingMapper.toResponse(savedBooking, room);
    }

    public BookingResponse cancelBooking(CancelBookingRequest request, String requesterId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!isStaff(requester) && !booking.getUserId().equals(requesterId)) {
            throw new UnauthorizedActionException("You are not allowed to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingNotFoundException("Booking has already been cancelled");
        }

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

        return BookingMapper.toResponse(updatedBooking, room);
    }

    public BookingResponse getBookingById(String bookingId, String requesterId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!isStaff(requester) && !booking.getUserId().equals(requesterId)) {
            throw new UnauthorizedActionException("You are not allowed to access this booking");
        }

        Room room = booking.getRoomId() != null
                ? roomRepository.findById(booking.getRoomId()).orElse(null)
                : null;
        return BookingMapper.toResponse(booking, room);
    }

    public List<BookingResponse> getBookingsByUser(String userId, String requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!isStaff(requester) && !requester.getId().equals(userId)) {
            throw new UnauthorizedActionException("You are not allowed to access these bookings");
        }

        return enrichWithRoomNumbers(bookingRepository.findByUserId(userId));
    }

    public List<BookingResponse> getBookingsByRoom(String roomId, String requesterId) {
        requireStaff(requesterId, "Only staff can view bookings by room");

        return enrichWithRoomNumbers(bookingRepository.findByRoomId(roomId));
    }

    public List<BookingResponse> getBookingsByStatus(BookingStatus status, String requesterId) {
        requireStaff(requesterId, "Only staff can view bookings by status");

        return enrichWithRoomNumbers(bookingRepository.findByStatus(status));
    }

    private List<BookingResponse> enrichWithRoomNumbers(List<Booking> bookings) {
        List<String> roomIds = bookings.stream()
                .map(Booking::getRoomId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        Map<String, Room> roomsById = roomRepository.findAllById(roomIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(Room::getId, Function.identity()));

        return bookings.stream()
                .map(b -> BookingMapper.toResponse(b, roomsById.get(b.getRoomId())))
                .toList();
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

    private void validateBookingRequest(CreateBookingRequest request) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new UserIdCannotBeEmptyException("User ID cannot be empty");
        }
        if (request.getRoomType() == null) {
            throw new RoomIdCannotBeEmptyException("Room type cannot be empty");
        }
        if (request.getCheckInDate() == null || request.getCheckOutDate() == null) {
            throw new DatesCannotBeEmptyException("Dates cannot be empty");
        }
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new CheckOutAfterCheckInException("Check-out must be after check-in");
        }
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new InvalidBookingDurationException("Check-in date cannot be in the past");
        }
    }

    private boolean isRoomFree(String roomId, LocalDate checkIn, LocalDate checkOut) {
        List<Booking> bookings = bookingRepository.findByRoomId(roomId);

        for (Booking existing : bookings) {
            if (existing.getStatus() == BookingStatus.CANCELLED) continue;

            boolean overlaps = !(checkOut.isBefore(existing.getCheckInDate()) || checkIn.isAfter(existing.getCheckOutDate()));

            if (overlaps) return false;
        }
        return true;
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
