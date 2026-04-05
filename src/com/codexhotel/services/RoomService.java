package com.codexhotel.services;

import com.codexhotel.data.enums.Role;
import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.models.Room;
import com.codexhotel.data.models.User;
import com.codexhotel.data.repositories.RoomRepository;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.requests.CreateRoomRequest;
import com.codexhotel.dtos.requests.UpdateRoomStatusRequest;
import com.codexhotel.dtos.responses.RoomResponse;
import com.codexhotel.exceptions.*;
import com.codexhotel.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public RoomResponse createRoom(CreateRoomRequest request, String callerUserId) {
        checkAdmin(callerUserId);
        validateRoomRequest(request);

        if (roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
            throw new RoomNumberAlreadyExistsException("Room number already exists");
        }

        Room room = RoomMapper.toRoom(request);
        Room savedRoom = roomRepository.save(room);

        return RoomMapper.toResponse(savedRoom);
    }

    public RoomResponse getRoomById(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        return RoomMapper.toResponse(room);
    }

    public RoomResponse getRoomByNumber(int roomNumber) {
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        return RoomMapper.toResponse(room);
    }

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(RoomMapper::toResponse)
                .toList();
    }

    public List<RoomResponse> getRoomsByStatus(RoomStatus status) {
        return roomRepository.findByStatus(status)
                .stream()
                .map(RoomMapper::toResponse)
                .toList();
    }

    public RoomResponse updateRoomStatus(UpdateRoomStatusRequest request, String callerUserId) {
        checkAdmin(callerUserId);

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        room.setStatus(request.getRoomStatus());
        Room savedRoom = roomRepository.save(room);

        return RoomMapper.toResponse(savedRoom);
    }

    public void deleteRoom(String id, String callerUserId) {
        checkAdmin(callerUserId);

        if (!roomRepository.existsById(id)) {
            throw new RoomNotFoundException("Room not found");
        }
        roomRepository.deleteById(id);
    }

    private void validateRoomRequest(CreateRoomRequest request) {
        if (request.getRoomNumber() <= 0) {
            throw new InvalidRoomRequestException("Room number must be greater than 0");
        }
        if (request.getBasePrice() < 0) {
            throw new InvalidBasePriceException("Base price cannot be less than 0");
        }
        if (request.getRoomType() == null) {
            throw new RoomTypeCannotBeEmptyException("Room type cannot be empty");
        }
    }

    private void checkAdmin(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Admin privileges required");
        }
    }
}