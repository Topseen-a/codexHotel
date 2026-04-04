package com.codexhotel.data.repositories;

import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.models.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {

    Optional<Room> findByRoomNumber(int roomNumber);

    List<Room> findByStatus(RoomStatus status);
}
