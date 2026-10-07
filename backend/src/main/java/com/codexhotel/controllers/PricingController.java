package com.codexhotel.controllers;

import com.codexhotel.data.enums.RoomType;
import com.codexhotel.dtos.responses.ApiResponse;
import com.codexhotel.dtos.responses.PricingResponse;
import com.codexhotel.services.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/calculate")
    public ApiResponse<Double> calculatePrice(@RequestParam RoomType roomType,
                                               @RequestParam double basePrice,
                                               @RequestParam String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        double price = pricingService.calculatePrice(roomType, basePrice, parsedDate);
        return ApiResponse.success(price);
    }

    @GetMapping
    public ApiResponse<List<PricingResponse>> getPriceList() {
        return ApiResponse.success(pricingService.getPriceList());
    }
}
