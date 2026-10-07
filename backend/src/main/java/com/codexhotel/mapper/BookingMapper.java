package com.codexhotel.mapper;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.models.Booking;
import com.codexhotel.data.models.Room;
import com.codexhotel.dtos.requests.CreateBookingRequest;
import com.codexhotel.dtos.responses.BookingResponse;

import java.time.LocalDate;

public class BookingMapper {

    public static Booking toBooking(CreateBookingRequest request) {
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setRoomType(request.getRoomType());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setCreatedAt(LocalDate.now());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalPrice(0.0);
        return booking;
    }

    /**
     * Use this overload whenever the associated Room is available, so the
     * response can carry the human-readable room number instead of just its id.
     */
    public static BookingResponse toResponse(Booking booking, Room room) {
        BookingResponse response = toResponse(booking);
        if (room != null) {
            response.setRoomNumber(room.getRoomNumber());
        }
        return response;
    }

    public static BookingResponse toResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getId());
        response.setUserId(booking.getUserId());
        response.setRoomId(booking.getRoomId());
        response.setRoomType(booking.getRoomType());
        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setStatus(booking.getStatus());
        response.setTotalPrice(booking.getTotalPrice());
        return response;
    }
}
