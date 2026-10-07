package com.codexhotel.repository;

import com.codexhotel.enums.RoomStatus;
import com.codexhotel.enums.RoomType;
import com.codexhotel.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {

    Optional<Room> findByRoomNumber(int roomNumber);

    boolean existsByRoomNumber(int roomNumber);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByType(RoomType type);
}
