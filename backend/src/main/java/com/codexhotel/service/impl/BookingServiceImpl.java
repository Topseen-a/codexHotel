package com.codexhotel.service.impl;

import com.codexhotel.dto.request.CancelBookingRequest;
import com.codexhotel.dto.request.CreateBookingRequest;
import com.codexhotel.dto.response.BookingResponse;
import com.codexhotel.enums.BookingStatus;
import com.codexhotel.enums.RoomStatus;
import com.codexhotel.exception.BadRequestException;
import com.codexhotel.exception.ConflictException;
import com.codexhotel.exception.ResourceNotFoundException;
import com.codexhotel.mapper.BookingMapper;
import com.codexhotel.model.Booking;
import com.codexhotel.model.Room;
import com.codexhotel.model.User;
import com.codexhotel.notification.NotificationManager;
import com.codexhotel.repository.BookingRepository;
import com.codexhotel.repository.RoomRepository;
import com.codexhotel.security.AccessGuard;
import com.codexhotel.service.BookingService;
import com.codexhotel.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PricingService pricingService;
    private final AccessGuard accessGuard;
    private final NotificationManager notificationManager;

    @Override
    public BookingResponse createBooking(CreateBookingRequest request, String requesterId) {
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new BadRequestException("Check-out must be after check-in");
        }

        accessGuard.requireOwnerOrStaff(requesterId, request.getUserId(), "You can only create bookings for yourself");
        User guest = accessGuard.requireUser(request.getUserId());

        List<Room> rooms = roomRepository.findByType(request.getRoomType());
        if (rooms.isEmpty()) {
            throw new ResourceNotFoundException("No rooms found for this type");
        }

        Room room = rooms.stream()
                .filter(r -> r.getStatus() != RoomStatus.MAINTENANCE)
                .filter(r -> !bookingRepository.existsOverlappingBooking(
                        r.getId(), request.getCheckInDate(), request.getCheckOutDate()))
                .findFirst()
                .orElseThrow(() -> new ConflictException("No available room for selected dates"));

        Booking booking = BookingMapper.toBooking(request);
        booking.setRoomId(room.getId());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalPrice(calculateTotalPrice(room, request.getCheckInDate(), request.getCheckOutDate()));
        booking.setCreatedAt(LocalDate.now());

        Booking savedBooking = bookingRepository.save(booking);

        notificationManager.notifyByEmailAndSms(guest.getEmail(), guest.getPhoneNumber(),
                "Your booking for room " + room.getRoomNumber() +
                        " from " + booking.getCheckInDate() + " to " + booking.getCheckOutDate() +
                        " has been CONFIRMED. Total price: N" + booking.getTotalPrice());

        return BookingMapper.toResponse(savedBooking, room);
    }

    @Override
    public BookingResponse cancelBooking(CancelBookingRequest request, String requesterId) {
        Booking booking = findBooking(request.getBookingId());

        accessGuard.requireOwnerOrStaff(requesterId, booking.getUserId(), "You are not allowed to cancel this booking");

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking has already been cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);

        Room room = roomRepository.findById(booking.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        User guest = accessGuard.requireUser(booking.getUserId());
        notificationManager.notifyByEmailAndSms(guest.getEmail(), guest.getPhoneNumber(),
                "Your booking for room " + room.getRoomNumber() +
                        " from " + booking.getCheckInDate() + " to " + booking.getCheckOutDate() +
                        " has been CANCELLED.");

        return BookingMapper.toResponse(updatedBooking, room);
    }

    @Override
    public BookingResponse getBookingById(String bookingId, String requesterId) {
        Booking booking = findBooking(bookingId);

        accessGuard.requireOwnerOrStaff(requesterId, booking.getUserId(), "You are not allowed to access this booking");

        Room room = booking.getRoomId() != null
                ? roomRepository.findById(booking.getRoomId()).orElse(null)
                : null;
        return BookingMapper.toResponse(booking, room);
    }

    @Override
    public List<BookingResponse> getBookingsByUser(String userId, String requesterId) {
        accessGuard.requireOwnerOrStaff(requesterId, userId, "You are not allowed to access these bookings");

        return toResponsesWithRoomNumbers(bookingRepository.findByUserId(userId));
    }

    @Override
    public List<BookingResponse> getBookingsByRoom(String roomId, String requesterId) {
        accessGuard.requireStaff(requesterId, "Only staff can view bookings by room");

        return toResponsesWithRoomNumbers(bookingRepository.findByRoomId(roomId));
    }

    @Override
    public List<BookingResponse> getBookingsByStatus(BookingStatus status, String requesterId) {
        accessGuard.requireStaff(requesterId, "Only staff can view bookings by status");

        return toResponsesWithRoomNumbers(bookingRepository.findByStatus(status));
    }

    private Booking findBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private List<BookingResponse> toResponsesWithRoomNumbers(List<Booking> bookings) {
        List<String> roomIds = bookings.stream()
                .map(Booking::getRoomId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, Room> roomsById = roomRepository.findAllById(roomIds)
                .stream()
                .collect(Collectors.toMap(Room::getId, Function.identity()));

        return bookings.stream()
                .map(booking -> BookingMapper.toResponse(booking, roomsById.get(booking.getRoomId())))
                .toList();
    }

    private BigDecimal calculateTotalPrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        BigDecimal total = BigDecimal.ZERO;
        for (LocalDate night = checkIn; night.isBefore(checkOut); night = night.plusDays(1)) {
            total = total.add(pricingService.calculatePrice(room.getType(), room.getBasePrice(), night));
        }
        return total;
    }
}
