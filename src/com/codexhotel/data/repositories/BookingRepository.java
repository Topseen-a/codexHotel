package com.codexhotel.data.repositories;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.models.Booking;
import com.codexhotel.data.models.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByRoomId(String roomId);

    List<Booking> findByUserId(String userId);

    List<Booking> findByCheckInDateBetween(LocalDate start, LocalDate end);

    List<Booking> findByStatus(BookingStatus status);
}
