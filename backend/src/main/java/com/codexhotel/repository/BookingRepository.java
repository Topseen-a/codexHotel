package com.codexhotel.repository;

import com.codexhotel.enums.BookingStatus;
import com.codexhotel.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByRoomId(String roomId);

    List<Booking> findByUserId(String userId);

    List<Booking> findByStatus(BookingStatus status);

    @Query(value = "{ 'roomId': ?0, 'status': { $ne: 'CANCELLED' }, 'checkInDate': { $lt: ?2 }, 'checkOutDate': { $gt: ?1 } }",
            exists = true)
    boolean existsOverlappingBooking(String roomId, LocalDate checkIn, LocalDate checkOut);
}
