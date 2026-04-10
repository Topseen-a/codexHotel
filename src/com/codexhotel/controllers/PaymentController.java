package com.codexhotel.controllers;

import com.codexhotel.dtos.requests.PaymentRequest;
import com.codexhotel.dtos.responses.PaymentResponse;
import com.codexhotel.exceptions.*;
import com.codexhotel.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> makePayment(@RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = paymentService.makePayment(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (BookingNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BookingIdCannotBeEmptyException | AmountCannotBeLessThanZeroException | PaymentMethodCannotBeEmptyException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable String paymentId, @RequestParam String requesterId) {
        try {
            PaymentResponse response = paymentService.getPaymentById(paymentId, requesterId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PaymentNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getPaymentsByBookingId(@PathVariable String bookingId, @RequestParam String requesterId) {
        try {
            List<PaymentResponse> response = paymentService.getPaymentsByBookingId(bookingId, requesterId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BookingNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getPaymentsByStatus(@RequestParam boolean successful, @RequestParam String requesterId) {
        try {
            List<PaymentResponse> response = paymentService.getPaymentsByStatus(successful, requesterId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{paymentId}/success")
    public ResponseEntity<?> markPaymentAsSuccessful(@PathVariable String paymentId, @RequestParam String requesterId) {
        try {
            PaymentResponse response = paymentService.markPaymentAsSuccessful(paymentId, requesterId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PaymentNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<?> deletePayment(@PathVariable String paymentId, @RequestParam String requesterId) {
        try {
            paymentService.deletePaymentById(paymentId, requesterId);
            return new ResponseEntity<>("Payment deleted successfully", HttpStatus.OK);
        } catch (PaymentNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AdminAccessRequiredException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}