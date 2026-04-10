package com.codexhotel.controllers;

import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.dtos.requests.CreateRoomRequest;
import com.codexhotel.dtos.requests.UpdateRoomStatusRequest;
import com.codexhotel.dtos.responses.RoomResponse;
import com.codexhotel.exceptions.*;
import com.codexhotel.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request, @RequestParam String callerUserId) {
        try {
            RoomResponse response = roomService.createRoom(request, callerUserId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (RoomNumberAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (InvalidRoomRequestException | InvalidBasePriceException | RoomTypeCannotBeEmptyException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable String id) {
        try {
            RoomResponse response = roomService.getRoomById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RoomNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<?> getRoomByNumber(@PathVariable int roomNumber) {
        try {
            RoomResponse response = roomService.getRoomByNumber(roomNumber);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RoomNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRooms() {
        try {
            List<RoomResponse> response = roomService.getAllRooms();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getRoomsByStatus(@RequestParam RoomStatus status) {
        try {
            List<RoomResponse> response = roomService.getRoomsByStatus(status);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/status")
    public ResponseEntity<?> updateRoomStatus(@RequestBody UpdateRoomStatusRequest request, @RequestParam String callerUserId) {
        try {
            RoomResponse response = roomService.updateRoomStatus(request, callerUserId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (RoomNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable String id, @RequestParam String callerUserId) {
        try {
            roomService.deleteRoom(id, callerUserId);
            return new ResponseEntity<>("Room deleted successfully", HttpStatus.OK);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (RoomNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}