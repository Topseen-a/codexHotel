package com.codexhotel.services;

import com.codexhotel.data.enums.Role;
import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.enums.RoomType;
import com.codexhotel.data.repositories.RoomRepository;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.requests.CreateRoomRequest;
import com.codexhotel.dtos.requests.CreateUserRequest;
import com.codexhotel.dtos.requests.UpdateRoomStatusRequest;
import com.codexhotel.dtos.responses.RoomResponse;
import com.codexhotel.dtos.responses.UserResponse;
import com.codexhotel.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testThatAdminCanCreateA_room() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(5_000);

        RoomResponse response = roomService.createRoom(request, savedAdmin.getId());

        assertEquals(101, response.getRoomNumber());
    }

    @Test
    public void testThatCreateRoomByNonAdminThrowsException() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setName("Oluwaseun");
        userRequest.setEmail("oluwaseun@gmail.com");
        userRequest.setPhoneNumber("08012345678");
        userRequest.setRole(Role.GUEST);

        UserResponse savedUser = userService.createUser(userRequest);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(5_000);

        assertThrows(UnauthorizedActionException.class, () -> roomService.createRoom(request, savedUser.getId()));
    }

    @Test
    public void testThatCreateRoomWithDuplicateRoomNumberThrowsException() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest requestOne = new CreateRoomRequest();
        requestOne.setRoomNumber(101);
        requestOne.setRoomType(RoomType.STANDARD);
        requestOne.setRoomStatus(RoomStatus.AVAILABLE);
        requestOne.setBasePrice(5_000);

        roomService.createRoom(requestOne, savedAdmin.getId());

        CreateRoomRequest requestTwo = new CreateRoomRequest();
        requestTwo.setRoomNumber(101);
        requestTwo.setRoomType(RoomType.DELUXE);
        requestTwo.setRoomStatus(RoomStatus.AVAILABLE);
        requestTwo.setBasePrice(3_000);

        assertThrows(RoomNumberAlreadyExistsException.class, () -> roomService.createRoom(requestTwo, savedAdmin.getId()));
    }

    @Test
    public void testThatCreateRoomWithInvalidRoomNumberThrowsException() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(0);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(5_000);

        assertThrows(InvalidRoomRequestException.class, () -> roomService.createRoom(request, savedAdmin.getId()));
    }

    @Test
    public void testThatRoomCanBeGottenById() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(5_000);

        RoomResponse savedRoom = roomService.createRoom(request, savedAdmin.getId());

        RoomResponse response = roomService.getRoomById(savedRoom.getId());

        assertEquals(savedRoom.getId(), response.getId());
    }

    @Test
    public void testThatGetRoomByUncreatedRoomIdThrowsException() {
        assertThrows(RoomNotFoundException.class, () -> roomService.getRoomById("123-456"));
    }

    @Test
    public void testThatRoomCanBeGottenByRoomNumber() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(5_000);

        RoomResponse savedRoom = roomService.createRoom(request, savedAdmin.getId());

        RoomResponse response = roomService.getRoomByNumber(101);

        assertEquals(101, response.getRoomNumber());
    }

    @Test
    public void testThatGetRoomByUncreatedRoomNumberThrowsException() {
        assertThrows(RoomNotFoundException.class, () -> roomService.getRoomByNumber(999));
    }

    @Test
    public void testThatRoomCanBeGottenByStatus() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest roomOneRequest = new CreateRoomRequest();
        roomOneRequest.setRoomNumber(301);
        roomOneRequest.setRoomType(RoomType.STANDARD);
        roomOneRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomOneRequest.setBasePrice(5_000);

        roomService.createRoom(roomOneRequest, savedAdmin.getId());

        CreateRoomRequest roomTwoRequest = new CreateRoomRequest();
        roomTwoRequest.setRoomNumber(302);
        roomTwoRequest.setRoomType(RoomType.SUITE);
        roomTwoRequest.setRoomStatus(RoomStatus.OCCUPIED);
        roomTwoRequest.setBasePrice(15_000);

        roomService.createRoom(roomTwoRequest, savedAdmin.getId());

        List<RoomResponse> availableRooms = roomService.getRoomsByStatus(RoomStatus.AVAILABLE);
        List<RoomResponse> occupiedRooms = roomService.getRoomsByStatus(RoomStatus.OCCUPIED);

        assertEquals(1, availableRooms.size());
        assertEquals(1, occupiedRooms.size());
    }

    @Test
    public void testThatAllRoomsCanBeGotten() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(5_000);

        roomService.createRoom(request, savedAdmin.getId());

        List<RoomResponse> rooms = roomService.getAllRooms();
        assertEquals(1, rooms.size());
    }

    @Test
    public void testThatRoomStatusCanBeUpdatedByAdmin() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomNumber(101);
        roomRequest.setRoomType(RoomType.STANDARD);
        roomRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomRequest.setBasePrice(5_000);

        RoomResponse savedRoom = roomService.createRoom(roomRequest, savedAdmin.getId());

        UpdateRoomStatusRequest updateRequest = new UpdateRoomStatusRequest();
        updateRequest.setRoomId(savedRoom.getId());
        updateRequest.setRoomStatus(RoomStatus.OCCUPIED);

        RoomResponse updated = roomService.updateRoomStatus(updateRequest, savedAdmin.getId());

        assertEquals(RoomStatus.OCCUPIED, updated.getStatus());
    }

    @Test
    public void testThatUpdateRoomStatusByNonAdminThrowsException() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setName("Oluwaseun");
        userRequest.setEmail("oluwaseun@gmail.com");
        userRequest.setPhoneNumber("08012345678");
        userRequest.setRole(Role.GUEST);

        UserResponse savedUser = userService.createUser(userRequest);

        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomNumber(101);
        roomRequest.setRoomType(RoomType.STANDARD);
        roomRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomRequest.setBasePrice(5_000);

        RoomResponse savedRoom = roomService.createRoom(roomRequest, savedAdmin.getId());

        UpdateRoomStatusRequest updateRequest = new UpdateRoomStatusRequest();
        updateRequest.setRoomId(savedRoom.getId());
        updateRequest.setRoomStatus(RoomStatus.OCCUPIED);

        assertThrows(UnauthorizedActionException.class, () -> roomService.updateRoomStatus(updateRequest, savedUser.getId()));
    }

    @Test
    public void testThatAdminCanDeleteRoom() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomNumber(101);
        roomRequest.setRoomType(RoomType.STANDARD);
        roomRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomRequest.setBasePrice(5_000);

        RoomResponse savedRoom = roomService.createRoom(roomRequest, savedAdmin.getId());

        roomService.deleteRoom(savedRoom.getId(), savedAdmin.getId());

        assertEquals(0, roomRepository.count());
    }

    @Test
    public void testThatDeleteRoomByNonAdminThrowsException() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setName("Oluwaseun");
        userRequest.setEmail("oluwaseun@gmail.com");
        userRequest.setPhoneNumber("08012345678");
        userRequest.setRole(Role.GUEST);

        UserResponse savedUser = userService.createUser(userRequest);

        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Admin");
        adminRequest.setEmail("admin@gmail.com");
        adminRequest.setPhoneNumber("08011111111");
        adminRequest.setRole(Role.ADMIN);

        UserResponse savedAdmin = userService.createUser(adminRequest);

        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomNumber(101);
        roomRequest.setRoomType(RoomType.STANDARD);
        roomRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomRequest.setBasePrice(5_000);

        RoomResponse savedRoom = roomService.createRoom(roomRequest, savedAdmin.getId());

        assertThrows(UnauthorizedActionException.class, () -> roomService.deleteRoom(savedRoom.getId(), savedUser.getId()));
    }
}