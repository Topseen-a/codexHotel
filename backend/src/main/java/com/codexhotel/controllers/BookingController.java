package com.codexhotel.controllers;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.dtos.requests.CancelBookingRequest;
import com.codexhotel.dtos.requests.CreateBookingRequest;
import com.codexhotel.dtos.responses.ApiResponse;
import com.codexhotel.dtos.responses.BookingResponse;
import com.codexhotel.security.SecurityUtils;
import com.codexhotel.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody CreateBookingRequest request,
                                                                        Authentication authentication) {
        BookingResponse response = bookingService.createBooking(request, SecurityUtils.currentUserId(authentication));
        return new ResponseEntity<>(ApiResponse.success("Booking created", response), HttpStatus.CREATED);
    }

    @PutMapping("/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@Valid @RequestBody CancelBookingRequest request,
                                                                         Authentication authentication) {
        BookingResponse response = bookingService.cancelBooking(request, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled", response));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable String bookingId,
                                                                          Authentication authentication) {
        BookingResponse response = bookingService.getBookingById(bookingId, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByUser(@PathVariable String userId,
                                                                                   Authentication authentication) {
        List<BookingResponse> response = bookingService.getBookingsByUser(userId, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByRoom(@PathVariable String roomId,
                                                                                   Authentication authentication) {
        List<BookingResponse> response = bookingService.getBookingsByRoom(roomId, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByStatus(@RequestParam BookingStatus status,
                                                                                     Authentication authentication) {
        List<BookingResponse> response = bookingService.getBookingsByStatus(status, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
