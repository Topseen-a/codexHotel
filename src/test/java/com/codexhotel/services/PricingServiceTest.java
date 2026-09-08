package com.codexhotel.services;

import com.codexhotel.data.enums.RoomType;
import com.codexhotel.data.enums.Season;
import com.codexhotel.data.models.Pricing;
import com.codexhotel.data.repositories.PricingRepository;
import com.codexhotel.dtos.responses.PricingResponse;
import com.codexhotel.exceptions.PricingNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PricingServiceTest {

    @Autowired
    private PricingService pricingService;

    @Autowired
    private PricingRepository pricingRepository;

    @BeforeEach
    public void setUp() {
        pricingRepository.deleteAll();
    }

    @Test
    public void estThatCalculatePriceOnWeekdayReturnsPrice() {
        Pricing pricing = new Pricing();
        pricing.setRoomType(RoomType.STANDARD);
        pricing.setSeason(Season.WEEKDAY);
        pricing.setMultiplier(1);

        pricingRepository.save(pricing);

        double price = pricingService.calculatePrice(RoomType.STANDARD, 5_000, LocalDate.of(2026, 4, 6));

        assertEquals(5_000, price);
    }

    @Test
    public void testThatCalculatePriceOnWeekendReturnsPrice() {
        Pricing pricing = new Pricing();
        pricing.setRoomType(RoomType.STANDARD);
        pricing.setSeason(Season.WEEKEND);
        pricing.setMultiplier(2);

        pricingRepository.save(pricing);

        double price = pricingService.calculatePrice(RoomType.STANDARD, 5_000, LocalDate.of(2026, 4, 5));

        assertEquals(10_000, price);
    }

    @Test
    public void testThatCalculatePriceOnFestivePeriodReturnsPrice() {
        Pricing pricing = new Pricing();
        pricing.setRoomType(RoomType.STANDARD);
        pricing.setSeason(Season.FESTIVE);
        pricing.setMultiplier(3);

        pricingRepository.save(pricing);

        double price = pricingService.calculatePrice(RoomType.STANDARD, 5_000, LocalDate.of(2026, 12, 25));

        assertEquals(15_000, price);
    }

    @Test
    public void testThatCalculatePriceWithoutSeasonThrowsException() {
        assertThrows(PricingNotFoundException.class, () -> pricingService.calculatePrice(RoomType.SUITE, 10_000, LocalDate.now()));
    }

    @Test
    public void testThatPriceListForRoomTypesCanBeGotten() {
        Pricing pricingOne = new Pricing();
        pricingOne.setRoomType(RoomType.STANDARD);
        pricingOne.setSeason(Season.WEEKDAY);
        pricingOne.setMultiplier(1);

        pricingRepository.save(pricingOne);

        Pricing pricingTwo = new Pricing();
        pricingTwo.setRoomType(RoomType.SUITE);
        pricingTwo.setSeason(Season.WEEKEND);
        pricingTwo.setMultiplier(2);

        pricingRepository.save(pricingTwo);

        List<PricingResponse> result = pricingService.getPriceList();

        assertEquals(2, result.size());

        boolean foundStandard = false;
        boolean foundSuite = false;

        for (PricingResponse response : result) {
            if (response.getRoomType() == RoomType.STANDARD && response.getSeason() == Season.WEEKDAY) {
                assertEquals(5000, response.getPrice());
                foundStandard = true;
            }
            if (response.getRoomType() == RoomType.SUITE && response.getSeason() == Season.WEEKEND) {
                assertEquals(30_000, response.getPrice());
                foundSuite = true;
            }
        }

        assertTrue(foundStandard);
        assertTrue(foundSuite);
    }
}
