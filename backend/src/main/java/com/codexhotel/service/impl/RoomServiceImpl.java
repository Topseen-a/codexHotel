package com.codexhotel.service.impl;

import com.codexhotel.dto.request.CreateRoomRequest;
import com.codexhotel.dto.request.UpdateRoomStatusRequest;
import com.codexhotel.dto.response.RoomResponse;
import com.codexhotel.enums.Role;
import com.codexhotel.enums.RoomStatus;
import com.codexhotel.exception.ConflictException;
import com.codexhotel.exception.ResourceNotFoundException;
import com.codexhotel.mapper.RoomMapper;
import com.codexhotel.model.Room;
import com.codexhotel.repository.RoomRepository;
import com.codexhotel.security.AccessGuard;
import com.codexhotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private static final String ROOM_MANAGER_REQUIRED = "Manager or admin privileges required";

    private final RoomRepository roomRepository;
    private final AccessGuard accessGuard;

    @Override
    public RoomResponse createRoom(CreateRoomRequest request, String callerUserId) {
        accessGuard.requireRole(callerUserId, ROOM_MANAGER_REQUIRED, Role.ADMIN, Role.MANAGER);

        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new ConflictException("Room number already exists");
        }

        try {
            return RoomMapper.toResponse(roomRepository.save(RoomMapper.toRoom(request)));
        } catch (DuplicateKeyException ex) {
            throw new ConflictException("Room number already exists");
        }
    }

    @Override
    public RoomResponse getRoomById(String id) {
        return RoomMapper.toResponse(findRoom(id));
    }

    @Override
    public RoomResponse getRoomByNumber(int roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        return RoomMapper.toResponse(room);
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(RoomMapper::toResponse)
                .toList();
    }

    @Override
    public List<RoomResponse> getRoomsByStatus(RoomStatus status) {
        return roomRepository.findByStatus(status)
                .stream()
                .map(RoomMapper::toResponse)
                .toList();
    }

    @Override
    public RoomResponse updateRoomStatus(UpdateRoomStatusRequest request, String callerUserId) {
        accessGuard.requireStaff(callerUserId, "Staff privileges required");

        Room room = findRoom(request.getRoomId());
        room.setStatus(request.getRoomStatus());

        return RoomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    public void deleteRoom(String id, String callerUserId) {
        accessGuard.requireRole(callerUserId, ROOM_MANAGER_REQUIRED, Role.ADMIN, Role.MANAGER);

        roomRepository.delete(findRoom(id));
    }

    private Room findRoom(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }
}
