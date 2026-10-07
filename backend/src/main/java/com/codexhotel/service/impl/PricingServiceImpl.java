package com.codexhotel.service.impl;

import com.codexhotel.dto.response.PricingResponse;
import com.codexhotel.enums.RoomType;
import com.codexhotel.enums.Season;
import com.codexhotel.exception.ResourceNotFoundException;
import com.codexhotel.model.Pricing;
import com.codexhotel.repository.PricingRepository;
import com.codexhotel.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl implements PricingService {

    private final PricingRepository pricingRepository;

    @Override
    public BigDecimal calculatePrice(RoomType roomType, BigDecimal basePrice, LocalDate date) {
        Season season = determineSeason(date);

        Pricing pricing = pricingRepository.findByRoomTypeAndSeason(roomType, season)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pricing not found for room type: " + roomType + " and season: " + season));

        return applyMultiplier(basePrice, pricing.getMultiplier());
    }

    @Override
    public List<PricingResponse> getPriceList() {
        return pricingRepository.findAll()
                .stream()
                .map(pricing -> PricingResponse.builder()
                        .roomType(pricing.getRoomType())
                        .season(pricing.getSeason())
                        .price(applyMultiplier(pricing.getRoomType().getDefaultBasePrice(), pricing.getMultiplier()))
                        .build())
                .toList();
    }

    private BigDecimal applyMultiplier(BigDecimal basePrice, BigDecimal multiplier) {
        return basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private Season determineSeason(LocalDate date) {
        if (date.getMonth() == Month.DECEMBER) {
            return Season.FESTIVE;
        }

        DayOfWeek day = date.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return Season.WEEKEND;
        }

        return Season.WEEKDAY;
    }
}
