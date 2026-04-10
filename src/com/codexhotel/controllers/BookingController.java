package com.codexhotel.controllers;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.dtos.requests.CancelBookingRequest;
import com.codexhotel.dtos.requests.CreateBookingRequest;
import com.codexhotel.dtos.responses.BookingResponse;
import com.codexhotel.exceptions.*;
import com.codexhotel.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody CreateBookingRequest request) {
        try {
            BookingResponse response = bookingService.createBooking(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotFoundException | RoomNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (RoomNotAvailableException | DatesCannotBeEmptyException | CheckOutAfterCheckInException | UserIdCannotBeEmptyException | RoomIdCannotBeEmptyException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/cancel")
    public ResponseEntity<?> cancelBooking(@RequestBody CancelBookingRequest request, @RequestParam String requesterId) {
        try {
            BookingResponse response = bookingService.cancelBooking(request, requesterId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BookingNotFoundException | UserNotFoundException | RoomNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable String bookingId, @RequestParam String requesterId) {
        try {
            BookingResponse response = bookingService.getBookingById(bookingId, requesterId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BookingNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsByUser(@PathVariable String userId, @RequestParam String requesterId) {
        try {
            List<BookingResponse> response = bookingService.getBookingsByUser(userId, requesterId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<?> getBookingsByRoom(@PathVariable String roomId, @RequestParam String requesterId) {
        try {
            List<BookingResponse> response = bookingService.getBookingsByRoom(roomId, requesterId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getBookingsByStatus(@RequestParam BookingStatus status, @RequestParam String requesterId) {
        try {
            List<BookingResponse> response = bookingService.getBookingsByStatus(status, requesterId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}