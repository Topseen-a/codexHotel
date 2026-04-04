package com.codexhotel.data.repositories;

import com.codexhotel.data.enums.RoomType;
import com.codexhotel.data.enums.Season;
import com.codexhotel.data.models.Pricing;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PricingRepository extends MongoRepository<Pricing, String> {

    Optional<Pricing> findByRoomType(RoomType roomType);

    Optional<Pricing> findByRoomTypeAndSeason(RoomType roomType, Season season);
}
