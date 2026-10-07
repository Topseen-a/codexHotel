package com.codexhotel.controller;

import com.codexhotel.dto.request.PaymentRequest;
import com.codexhotel.dto.response.ApiResponse;
import com.codexhotel.dto.response.PaymentResponse;
import com.codexhotel.security.UserPrincipal;
import com.codexhotel.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> makePayment(@Valid @RequestBody PaymentRequest request,
                                                                    @AuthenticationPrincipal UserPrincipal principal) {
        PaymentResponse response = paymentService.makePayment(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment recorded", response));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable String paymentId,
                                                                       @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentById(paymentId, principal.getId())));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByBookingId(@PathVariable String bookingId,
                                                                                     @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsByBookingId(bookingId, principal.getId())));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByStatus(@RequestParam boolean successful,
                                                                                  @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsByStatus(successful, principal.getId())));
    }

    @PutMapping("/{paymentId}/success")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PaymentResponse>> markPaymentAsSuccessful(@PathVariable String paymentId,
                                                                                @AuthenticationPrincipal UserPrincipal principal) {
        PaymentResponse response = paymentService.markPaymentAsSuccessful(paymentId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Payment marked as successful", response));
    }

    @DeleteMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable String paymentId,
                                                           @AuthenticationPrincipal UserPrincipal principal) {
        paymentService.deletePaymentById(paymentId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Payment deleted successfully"));
    }
}
