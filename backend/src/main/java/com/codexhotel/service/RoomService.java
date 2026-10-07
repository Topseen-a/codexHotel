package com.codexhotel.service;

import com.codexhotel.dto.request.CreateRoomRequest;
import com.codexhotel.dto.request.UpdateRoomStatusRequest;
import com.codexhotel.dto.response.RoomResponse;
import com.codexhotel.enums.RoomStatus;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface RoomService {

    RoomResponse createRoom(@Valid CreateRoomRequest request, String callerUserId);

    RoomResponse getRoomById(String id);

    RoomResponse getRoomByNumber(int roomNumber);

    List<RoomResponse> getAllRooms();

    List<RoomResponse> getRoomsByStatus(RoomStatus status);

    RoomResponse updateRoomStatus(@Valid UpdateRoomStatusRequest request, String callerUserId);

    void deleteRoom(String id, String callerUserId);
}
