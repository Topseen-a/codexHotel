package com.codexhotel.controller;

import com.codexhotel.dto.response.ApiResponse;
import com.codexhotel.dto.response.PricingResponse;
import com.codexhotel.enums.RoomType;
import com.codexhotel.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/calculate")
    public ResponseEntity<ApiResponse<BigDecimal>> calculatePrice(@RequestParam RoomType roomType,
                                                              @RequestParam BigDecimal basePrice,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(pricingService.calculatePrice(roomType, basePrice, date)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PricingResponse>>> getPriceList() {
        return ResponseEntity.ok(ApiResponse.success(pricingService.getPriceList()));
    }
}
