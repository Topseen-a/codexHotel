package com.codexhotel.exceptions;

import com.codexhotel.dtos.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central place for turning domain/security exceptions into consistent
 * ApiResponse-shaped HTTP responses, instead of every controller repeating
 * the same try/catch blocks.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- 404 Not found ----
    @ExceptionHandler({
            UserNotFoundException.class,
            RoomNotFoundException.class,
            BookingNotFoundException.class,
            PaymentNotFoundException.class,
            PricingNotFoundException.class,
            CallerNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    // ---- 400 Bad request / validation ----
    @ExceptionHandler({
            DatesCannotBeEmptyException.class,
            CheckOutAfterCheckInException.class,
            InvalidBookingDurationException.class,
            UserIdCannotBeEmptyException.class,
            RoomIdCannotBeEmptyException.class,
            InvalidRoomRequestException.class,
            InvalidBasePriceException.class,
            RoomTypeCannotBeEmptyException.class,
            RoomStatusCannotBeEmptyException.class,
            BookingIdCannotBeEmptyException.class,
            AmountCannotBeLessThanZeroException.class,
            PaymentMethodCannotBeEmptyException.class,
            InvalidPaymentAmountException.class,
            NameCannotBeEmptyException.class,
            EmailCannotBeEmptyException.class,
            InvalidEmailException.class,
            PhoneNumberCannotBeEmptyException.class,
            InvalidPhoneNumberException.class,
            InvalidPasswordException.class,
            RoomNotAvailableException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    // ---- 409 Conflict ----
    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            RoomNumberAlreadyExistsException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }

    // ---- 401 Unauthorized (bad/missing credentials) ----
    @ExceptionHandler({
            InvalidCredentialsException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid email or password"));
    }

    // ---- 403 Forbidden (authenticated, but not allowed) ----
    @ExceptionHandler({
            UnauthorizedActionException.class,
            AccessDeniedException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleForbidden(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(ex.getMessage()));
    }

    // ---- 400 Bean validation failures on @Valid request bodies ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(ApiResponse.success("Validation failed", errors));
    }

    // ---- 500 Fallback ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError().body(ApiResponse.error("Something went wrong: " + ex.getMessage()));
    }
}
