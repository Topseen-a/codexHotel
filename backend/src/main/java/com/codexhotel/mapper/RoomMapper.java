package com.codexhotel.mapper;

import com.codexhotel.model.Room;
import com.codexhotel.dto.request.CreateRoomRequest;
import com.codexhotel.dto.response.RoomResponse;

public final class RoomMapper {

    private RoomMapper() {
    }

    public static Room toRoom(CreateRoomRequest request) {
        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getRoomType());
        room.setStatus(request.getRoomStatus());
        room.setBasePrice(request.getBasePrice());
        return room;
    }

    public static RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType())
                .basePrice(room.getBasePrice())
                .status(room.getStatus())
                .build();
    }
}
