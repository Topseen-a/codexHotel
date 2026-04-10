package com.codexhotel.controllers;

import com.codexhotel.data.enums.RoomType;
import com.codexhotel.dtos.responses.PricingResponse;
import com.codexhotel.exceptions.*;
import com.codexhotel.services.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/calculate")
    public ResponseEntity<?> calculatePrice(@RequestParam RoomType roomType, @RequestParam double basePrice, @RequestParam String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            double price = pricingService.calculatePrice(roomType, basePrice, parsedDate);
            return new ResponseEntity<>(price, HttpStatus.OK);
        } catch (InvalidRoomRequestException | InvalidBasePriceException | DatesCannotBeEmptyException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (PricingNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getPriceList() {
        try {
            List<PricingResponse> response = pricingService.getPriceList();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}