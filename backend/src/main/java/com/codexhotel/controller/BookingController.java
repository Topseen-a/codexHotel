package com.codexhotel.controller;

import com.codexhotel.dto.request.CancelBookingRequest;
import com.codexhotel.dto.request.CreateBookingRequest;
import com.codexhotel.dto.response.ApiResponse;
import com.codexhotel.dto.response.BookingResponse;
import com.codexhotel.enums.BookingStatus;
import com.codexhotel.security.UserPrincipal;
import com.codexhotel.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody CreateBookingRequest request,
                                                                      @AuthenticationPrincipal UserPrincipal principal) {
        BookingResponse response = bookingService.createBooking(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Booking created", response));
    }

    @PutMapping("/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@Valid @RequestBody CancelBookingRequest request,
                                                                      @AuthenticationPrincipal UserPrincipal principal) {
        BookingResponse response = bookingService.cancelBooking(request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled", response));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable String bookingId,
                                                                       @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingById(bookingId, principal.getId())));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByUser(@PathVariable String userId,
                                                                                @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingsByUser(userId, principal.getId())));
    }

    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByRoom(@PathVariable String roomId,
                                                                                @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingsByRoom(roomId, principal.getId())));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByStatus(@RequestParam BookingStatus status,
                                                                                  @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingsByStatus(status, principal.getId())));
    }
}
