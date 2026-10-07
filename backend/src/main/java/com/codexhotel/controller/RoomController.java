package com.codexhotel.controller;

import com.codexhotel.dto.request.CreateRoomRequest;
import com.codexhotel.dto.request.UpdateRoomStatusRequest;
import com.codexhotel.dto.response.ApiResponse;
import com.codexhotel.dto.response.RoomResponse;
import com.codexhotel.enums.RoomStatus;
import com.codexhotel.security.UserPrincipal;
import com.codexhotel.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody CreateRoomRequest request,
                                                                @AuthenticationPrincipal UserPrincipal principal) {
        RoomResponse response = roomService.createRoom(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Room created", response));
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
                                                                      @AuthenticationPrincipal UserPrincipal principal) {
        RoomResponse response = roomService.updateRoomStatus(request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Room status updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable String id,
                                                        @AuthenticationPrincipal UserPrincipal principal) {
        roomService.deleteRoom(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully"));
    }
}
