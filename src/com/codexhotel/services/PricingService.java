package com.codexhotel.services;

import com.codexhotel.data.enums.RoomType;
import com.codexhotel.data.enums.Season;
import com.codexhotel.data.models.Pricing;
import com.codexhotel.data.repositories.PricingRepository;
import com.codexhotel.exceptions.PricingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PricingRepository pricingRepository;

    public double calculatePrice(RoomType roomType, double basePrice, LocalDate date) {
        Season season = determineSeason(date);

        Pricing pricing = pricingRepository
                .findByRoomTypeAndSeason(roomType, season)
                .orElseThrow(() -> new PricingNotFoundException("Pricing not found"));

        return basePrice * pricing.getMultiplier();
    }

    private Season determineSeason(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();

        if (isFestivePeriod(date)) {
            return Season.FESTIVE;
        }
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return Season.WEEKEND;
        }

        return Season.WEEKDAY;
    }

    private boolean isFestivePeriod(LocalDate date) {
        int month = date.getMonthValue();

        return month == 12;
    }
}
