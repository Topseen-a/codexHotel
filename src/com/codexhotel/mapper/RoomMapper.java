package com.codexhotel.mapper;

import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.models.Room;
import com.codexhotel.dtos.requests.CreateRoomRequest;
import com.codexhotel.dtos.responses.RoomResponse;

public class RoomMapper {

    public static Room toRoom(CreateRoomRequest request) {
        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getRoomType());
        room.setBasePrice(request.getBasePrice());
        room.setStatus(RoomStatus.AVAILABLE);

        return room;
    }

    public static RoomResponse  toResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setType(room.getType());
        response.setBasePrice(room.getBasePrice());
        response.setStatus(room.getStatus());

        return response;
    }
}
