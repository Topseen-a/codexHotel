package com.codexhotel.data.repositories;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.models.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByRoomId(String roomId);

    List<Booking> findByUserId(String userId);

    List<Booking> findByStatus(BookingStatus status);
}
