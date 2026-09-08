package com.codexhotel.controllers;

import com.codexhotel.dtos.requests.PaymentRequest;
import com.codexhotel.dtos.responses.ApiResponse;
import com.codexhotel.dtos.responses.PaymentResponse;
import com.codexhotel.security.SecurityUtils;
import com.codexhotel.services.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> makePayment(@Valid @RequestBody PaymentRequest request,
                                                                       Authentication authentication) {
        PaymentResponse response = paymentService.makePayment(request, SecurityUtils.currentUserId(authentication));
        return new ResponseEntity<>(ApiResponse.success("Payment recorded", response), HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable String paymentId,
                                                                          Authentication authentication) {
        PaymentResponse response = paymentService.getPaymentById(paymentId, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByBookingId(@PathVariable String bookingId,
                                                                                        Authentication authentication) {
        List<PaymentResponse> response = paymentService.getPaymentsByBookingId(bookingId, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByStatus(@RequestParam boolean successful,
                                                                                     Authentication authentication) {
        List<PaymentResponse> response = paymentService.getPaymentsByStatus(successful, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{paymentId}/success")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PaymentResponse>> markPaymentAsSuccessful(@PathVariable String paymentId,
                                                                                   Authentication authentication) {
        PaymentResponse response = paymentService.markPaymentAsSuccessful(paymentId, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Payment marked as successful", response));
    }

    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable String paymentId, Authentication authentication) {
        paymentService.deletePaymentById(paymentId, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully", null));
    }
}
