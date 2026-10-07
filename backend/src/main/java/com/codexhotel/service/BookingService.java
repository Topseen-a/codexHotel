package com.codexhotel.service;

import com.codexhotel.dto.request.CancelBookingRequest;
import com.codexhotel.dto.request.CreateBookingRequest;
import com.codexhotel.dto.response.BookingResponse;
import com.codexhotel.enums.BookingStatus;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface BookingService {

    BookingResponse createBooking(@Valid CreateBookingRequest request, String requesterId);

    BookingResponse cancelBooking(@Valid CancelBookingRequest request, String requesterId);

    BookingResponse getBookingById(String bookingId, String requesterId);

    List<BookingResponse> getBookingsByUser(String userId, String requesterId);

    List<BookingResponse> getBookingsByRoom(String roomId, String requesterId);

    List<BookingResponse> getBookingsByStatus(BookingStatus status, String requesterId);
}
