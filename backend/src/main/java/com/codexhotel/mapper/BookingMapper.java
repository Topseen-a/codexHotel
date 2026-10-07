package com.codexhotel.mapper;

import com.codexhotel.model.Booking;
import com.codexhotel.model.Room;
import com.codexhotel.dto.request.CreateBookingRequest;
import com.codexhotel.dto.response.BookingResponse;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static Booking toBooking(CreateBookingRequest request) {
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setRoomType(request.getRoomType());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        return booking;
    }

    public static BookingResponse toResponse(Booking booking, Room room) {
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .userId(booking.getUserId())
                .roomId(booking.getRoomId())
                .roomNumber(room != null ? room.getRoomNumber() : 0)
                .roomType(booking.getRoomType())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .build();
    }
}
