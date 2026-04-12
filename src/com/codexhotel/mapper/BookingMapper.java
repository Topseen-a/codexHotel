package com.codexhotel.mapper;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.models.Booking;
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

    public static BookingResponse toResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getId());
        response.setUserId(booking.getUserId());
        booking.setRoomType(booking.getRoomType());
        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setStatus(booking.getStatus());
        response.setTotalPrice(booking.getTotalPrice());

        return response;
    }
}
