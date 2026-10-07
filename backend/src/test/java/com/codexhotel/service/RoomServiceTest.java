package com.codexhotel.service;

import com.codexhotel.enums.Role;
import com.codexhotel.enums.RoomStatus;
import com.codexhotel.enums.RoomType;
import com.codexhotel.model.User;
import com.codexhotel.repository.RoomRepository;
import com.codexhotel.repository.UserRepository;
import com.codexhotel.dto.request.CreateRoomRequest;
import com.codexhotel.dto.request.UpdateRoomStatusRequest;
import com.codexhotel.dto.response.RoomResponse;
import com.codexhotel.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User seedUser(String name, String email, String phone, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPassword("$2a$10$notARealHashJustForFixtures");
        user.setRole(role);
        return userRepository.save(user);
    }

    @Test
    public void testThatAdminCanCreateA_room() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(BigDecimal.valueOf(5_000));

        RoomResponse response = roomService.createRoom(request, admin.getId());

        assertEquals(101, response.getRoomNumber());
    }

    @Test
    public void testThatManagerCanCreateA_room() {
        User manager = seedUser("Manager Tunde", "tunde@gmail.com", "08033297107", Role.MANAGER);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(102);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(BigDecimal.valueOf(5_000));

        RoomResponse response = roomService.createRoom(request, manager.getId());

        assertEquals(102, response.getRoomNumber());
    }

    @Test
    public void testThatCreateRoomByNonAdminThrowsException() {
        User guest = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(BigDecimal.valueOf(5_000));

        assertThrows(ForbiddenException.class, () -> roomService.createRoom(request, guest.getId()));
    }

    @Test
    public void testThatCreateRoomByReceptionistThrowsException() {
        User receptionist = seedUser("Receptionist Amaka", "amaka@gmail.com", "08012345679", Role.RECEPTIONIST);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(BigDecimal.valueOf(5_000));

        assertThrows(ForbiddenException.class, () -> roomService.createRoom(request, receptionist.getId()));
    }

    @Test
    public void testThatCreateRoomWithDuplicateRoomNumberThrowsException() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        CreateRoomRequest requestOne = new CreateRoomRequest();
        requestOne.setRoomNumber(101);
        requestOne.setRoomType(RoomType.STANDARD);
        requestOne.setRoomStatus(RoomStatus.AVAILABLE);
        requestOne.setBasePrice(BigDecimal.valueOf(5_000));

        roomService.createRoom(requestOne, admin.getId());

        CreateRoomRequest requestTwo = new CreateRoomRequest();
        requestTwo.setRoomNumber(101);
        requestTwo.setRoomType(RoomType.DELUXE);
        requestTwo.setRoomStatus(RoomStatus.AVAILABLE);
        requestTwo.setBasePrice(BigDecimal.valueOf(3_000));

        assertThrows(ConflictException.class, () -> roomService.createRoom(requestTwo, admin.getId()));
    }

    @Test
    public void testThatCreateRoomWithInvalidRoomNumberThrowsException() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(0);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(BigDecimal.valueOf(5_000));

        assertThrows(ConstraintViolationException.class, () -> roomService.createRoom(request, admin.getId()));
    }

    @Test
    public void testThatRoomCanBeGottenById() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(BigDecimal.valueOf(5_000));

        RoomResponse savedRoom = roomService.createRoom(request, admin.getId());

        RoomResponse response = roomService.getRoomById(savedRoom.getId());

        assertEquals(savedRoom.getId(), response.getId());
    }

    @Test
    public void testThatGetRoomByUncreatedRoomIdThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomById("123-456"));
    }

    @Test
    public void testThatRoomCanBeGottenByRoomNumber() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(BigDecimal.valueOf(5_000));

        roomService.createRoom(request, admin.getId());

        RoomResponse response = roomService.getRoomByNumber(101);

        assertEquals(101, response.getRoomNumber());
    }

    @Test
    public void testThatGetRoomByUncreatedRoomNumberThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomByNumber(999));
    }

    @Test
    public void testThatRoomCanBeGottenByStatus() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        CreateRoomRequest roomOneRequest = new CreateRoomRequest();
        roomOneRequest.setRoomNumber(301);
        roomOneRequest.setRoomType(RoomType.STANDARD);
        roomOneRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomOneRequest.setBasePrice(BigDecimal.valueOf(5_000));

        roomService.createRoom(roomOneRequest, admin.getId());

        CreateRoomRequest roomTwoRequest = new CreateRoomRequest();
        roomTwoRequest.setRoomNumber(302);
        roomTwoRequest.setRoomType(RoomType.SUITE);
        roomTwoRequest.setRoomStatus(RoomStatus.OCCUPIED);
        roomTwoRequest.setBasePrice(BigDecimal.valueOf(15_000));

        roomService.createRoom(roomTwoRequest, admin.getId());

        List<RoomResponse> availableRooms = roomService.getRoomsByStatus(RoomStatus.AVAILABLE);
        List<RoomResponse> occupiedRooms = roomService.getRoomsByStatus(RoomStatus.OCCUPIED);

        assertEquals(1, availableRooms.size());
        assertEquals(1, occupiedRooms.size());
    }

    @Test
    public void testThatAllRoomsCanBeGotten() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber(101);
        request.setRoomType(RoomType.STANDARD);
        request.setRoomStatus(RoomStatus.AVAILABLE);
        request.setBasePrice(BigDecimal.valueOf(5_000));

        roomService.createRoom(request, admin.getId());

        List<RoomResponse> rooms = roomService.getAllRooms();
        assertEquals(1, rooms.size());
    }

    @Test
    public void testThatRoomStatusCanBeUpdatedByAdmin() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomNumber(101);
        roomRequest.setRoomType(RoomType.STANDARD);
        roomRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomRequest.setBasePrice(BigDecimal.valueOf(5_000));

        RoomResponse savedRoom = roomService.createRoom(roomRequest, admin.getId());

        UpdateRoomStatusRequest updateRequest = new UpdateRoomStatusRequest();
        updateRequest.setRoomId(savedRoom.getId());
        updateRequest.setRoomStatus(RoomStatus.OCCUPIED);

        RoomResponse updated = roomService.updateRoomStatus(updateRequest, admin.getId());

        assertEquals(RoomStatus.OCCUPIED, updated.getStatus());
    }

    @Test
    public void testThatRoomStatusCanBeUpdatedByReceptionist() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);
        User receptionist = seedUser("Receptionist Amaka", "amaka@gmail.com", "08012345679", Role.RECEPTIONIST);

        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomNumber(101);
        roomRequest.setRoomType(RoomType.STANDARD);
        roomRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomRequest.setBasePrice(BigDecimal.valueOf(5_000));

        RoomResponse savedRoom = roomService.createRoom(roomRequest, admin.getId());

        UpdateRoomStatusRequest updateRequest = new UpdateRoomStatusRequest();
        updateRequest.setRoomId(savedRoom.getId());
        updateRequest.setRoomStatus(RoomStatus.MAINTENANCE);

        RoomResponse updated = roomService.updateRoomStatus(updateRequest, receptionist.getId());

        assertEquals(RoomStatus.MAINTENANCE, updated.getStatus());
    }

    @Test
    public void testThatUpdateRoomStatusByNonAdminThrowsException() {
        User guest = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomNumber(101);
        roomRequest.setRoomType(RoomType.STANDARD);
        roomRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomRequest.setBasePrice(BigDecimal.valueOf(5_000));

        RoomResponse savedRoom = roomService.createRoom(roomRequest, admin.getId());

        UpdateRoomStatusRequest updateRequest = new UpdateRoomStatusRequest();
        updateRequest.setRoomId(savedRoom.getId());
        updateRequest.setRoomStatus(RoomStatus.OCCUPIED);

        assertThrows(ForbiddenException.class, () -> roomService.updateRoomStatus(updateRequest, guest.getId()));
    }

    @Test
    public void testThatAdminCanDeleteRoom() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomNumber(101);
        roomRequest.setRoomType(RoomType.STANDARD);
        roomRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomRequest.setBasePrice(BigDecimal.valueOf(5_000));

        RoomResponse savedRoom = roomService.createRoom(roomRequest, admin.getId());

        roomService.deleteRoom(savedRoom.getId(), admin.getId());

        assertEquals(0, roomRepository.count());
    }

    @Test
    public void testThatDeleteRoomByNonAdminThrowsException() {
        User guest = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);
        User admin = seedUser("Admin", "admin@gmail.com", "08011111111", Role.ADMIN);

        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomNumber(101);
        roomRequest.setRoomType(RoomType.STANDARD);
        roomRequest.setRoomStatus(RoomStatus.AVAILABLE);
        roomRequest.setBasePrice(BigDecimal.valueOf(5_000));

        RoomResponse savedRoom = roomService.createRoom(roomRequest, admin.getId());

        assertThrows(ForbiddenException.class, () -> roomService.deleteRoom(savedRoom.getId(), guest.getId()));
    }
}
