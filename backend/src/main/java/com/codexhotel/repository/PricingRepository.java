package com.codexhotel.repository;

import com.codexhotel.enums.RoomType;
import com.codexhotel.enums.Season;
import com.codexhotel.model.Pricing;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PricingRepository extends MongoRepository<Pricing, String> {

    Optional<Pricing> findByRoomTypeAndSeason(RoomType roomType, Season season);
}
