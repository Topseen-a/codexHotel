package com.codexhotel.services;

import com.codexhotel.data.enums.RoomType;
import com.codexhotel.data.enums.Season;
import com.codexhotel.data.models.Pricing;
import com.codexhotel.data.repositories.PricingRepository;
import com.codexhotel.dtos.responses.PricingResponse;
import com.codexhotel.exceptions.DatesCannotBeEmptyException;
import com.codexhotel.exceptions.InvalidBasePriceException;
import com.codexhotel.exceptions.InvalidRoomRequestException;
import com.codexhotel.exceptions.PricingNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PricingRepository pricingRepository;

    public double calculatePrice(RoomType roomType, double basePrice, LocalDate date) {
        if (roomType == null) throw new InvalidRoomRequestException("Room type cannot be empty");
        if (date == null) throw new DatesCannotBeEmptyException("Date cannot be empty");
        if (basePrice < 0) throw new InvalidBasePriceException("Base price cannot be negative");

        Season season = determineSeason(date);

        Pricing pricing = pricingRepository
                .findByRoomTypeAndSeason(roomType, season)
                .orElseThrow(() -> new PricingNotFoundException("Pricing not found for room type: " + roomType + " and season: " + season));

        return basePrice * pricing.getMultiplier();
    }

    public List<PricingResponse> getPriceList(double basePrice) {
        return pricingRepository.findAll()
                .stream()
                .map(p -> {
                    PricingResponse response = new PricingResponse();
                    response.setRoomType(p.getRoomType());
                    response.setSeason(p.getSeason());
                    response.setPrice(basePrice * p.getMultiplier());
                    return response;
                })
                .toList();
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
        if (date == null) throw new DatesCannotBeEmptyException("Date cannot be empty");

        int month = date.getMonthValue();
        return month == 12;
    }
}