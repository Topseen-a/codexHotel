package com.codexhotel.service;

import com.codexhotel.enums.RoomType;
import com.codexhotel.enums.Season;
import com.codexhotel.model.Pricing;
import com.codexhotel.repository.PricingRepository;
import com.codexhotel.dto.response.PricingResponse;
import com.codexhotel.exception.ResourceNotFoundException;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PricingServiceTest {

    @Autowired
    private PricingService pricingService;

    @Autowired
    private PricingRepository pricingRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setUp() {
        pricingRepository.deleteAll();
    }

    @Test
    public void estThatCalculatePriceOnWeekdayReturnsPrice() {
        Pricing pricing = new Pricing();
        pricing.setRoomType(RoomType.STANDARD);
        pricing.setSeason(Season.WEEKDAY);
        pricing.setMultiplier(BigDecimal.valueOf(1));

        pricingRepository.save(pricing);

        BigDecimal price = pricingService.calculatePrice(RoomType.STANDARD, BigDecimal.valueOf(5_000), LocalDate.of(2026, 4, 6));

        assertThat(price).isEqualByComparingTo("5000");
    }

    @Test
    public void testThatCalculatePriceOnWeekendReturnsPrice() {
        Pricing pricing = new Pricing();
        pricing.setRoomType(RoomType.STANDARD);
        pricing.setSeason(Season.WEEKEND);
        pricing.setMultiplier(BigDecimal.valueOf(2));

        pricingRepository.save(pricing);

        BigDecimal price = pricingService.calculatePrice(RoomType.STANDARD, BigDecimal.valueOf(5_000), LocalDate.of(2026, 4, 5));

        assertThat(price).isEqualByComparingTo("10000");
    }

    @Test
    public void testThatCalculatePriceOnFestivePeriodReturnsPrice() {
        Pricing pricing = new Pricing();
        pricing.setRoomType(RoomType.STANDARD);
        pricing.setSeason(Season.FESTIVE);
        pricing.setMultiplier(BigDecimal.valueOf(3));

        pricingRepository.save(pricing);

        BigDecimal price = pricingService.calculatePrice(RoomType.STANDARD, BigDecimal.valueOf(5_000), LocalDate.of(2026, 12, 25));

        assertThat(price).isEqualByComparingTo("15000");
    }

    @Test
    public void testThatCalculatePriceWithoutSeasonThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> pricingService.calculatePrice(RoomType.SUITE, BigDecimal.valueOf(10_000), LocalDate.now()));
    }

    @Test
    public void testThatPriceListForRoomTypesCanBeGotten() {
        Pricing pricingOne = new Pricing();
        pricingOne.setRoomType(RoomType.STANDARD);
        pricingOne.setSeason(Season.WEEKDAY);
        pricingOne.setMultiplier(BigDecimal.valueOf(1));

        pricingRepository.save(pricingOne);

        Pricing pricingTwo = new Pricing();
        pricingTwo.setRoomType(RoomType.SUITE);
        pricingTwo.setSeason(Season.WEEKEND);
        pricingTwo.setMultiplier(BigDecimal.valueOf(2));

        pricingRepository.save(pricingTwo);

        List<PricingResponse> result = pricingService.getPriceList();

        assertEquals(2, result.size());

        boolean foundStandard = false;
        boolean foundSuite = false;

        for (PricingResponse response : result) {
            if (response.getRoomType() == RoomType.STANDARD && response.getSeason() == Season.WEEKDAY) {
                assertThat(response.getPrice()).isEqualByComparingTo("5000");
                foundStandard = true;
            }
            if (response.getRoomType() == RoomType.SUITE && response.getSeason() == Season.WEEKEND) {
                assertThat(response.getPrice()).isEqualByComparingTo("30000");
                foundSuite = true;
            }
        }

        assertTrue(foundStandard);
        assertTrue(foundSuite);
    }

    @Test
    public void testThatCalculatePriceIsExactToTheKobo() {
        Pricing pricing = new Pricing();
        pricing.setRoomType(RoomType.STANDARD);
        pricing.setSeason(Season.WEEKEND);
        pricing.setMultiplier(new BigDecimal("1.3"));

        pricingRepository.save(pricing);

        BigDecimal price = pricingService.calculatePrice(RoomType.STANDARD, new BigDecimal("100.10"), LocalDate.of(2026, 4, 5));

        assertEquals(new BigDecimal("130.13"), price);
    }

    @Test
    public void testThatPricingStoredAsLegacyDoubleIsStillReadable() {
        mongoTemplate.getCollection("pricing").insertOne(new Document()
                .append("roomType", "SUITE")
                .append("season", "WEEKDAY")
                .append("multiplier", 1.5));

        BigDecimal price = pricingService.calculatePrice(RoomType.SUITE, BigDecimal.valueOf(10_000), LocalDate.of(2026, 4, 6));

        assertThat(price).isEqualByComparingTo("15000");
    }
}
