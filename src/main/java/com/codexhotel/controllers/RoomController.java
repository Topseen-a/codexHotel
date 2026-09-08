package com.codexhotel.controllers;

import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.dtos.requests.CreateRoomRequest;
import com.codexhotel.dtos.requests.UpdateRoomStatusRequest;
import com.codexhotel.dtos.responses.ApiResponse;
import com.codexhotel.dtos.responses.RoomResponse;
import com.codexhotel.security.SecurityUtils;
import com.codexhotel.services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody CreateRoomRequest request,
                                                                  Authentication authentication) {
        RoomResponse response = roomService.createRoom(request, SecurityUtils.currentUserId(authentication));
        return new ResponseEntity<>(ApiResponse.success("Room created", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomById(id)));
    }

    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomByNumber(@PathVariable int roomNumber) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomByNumber(roomNumber)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllRooms()));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByStatus(@RequestParam RoomStatus status) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomsByStatus(status)));
    }

    @PutMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoomStatus(@Valid @RequestBody UpdateRoomStatusRequest request,
                                                                        Authentication authentication) {
        RoomResponse response = roomService.updateRoomStatus(request, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Room status updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable String id, Authentication authentication) {
        roomService.deleteRoom(id, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully", null));
    }
}
