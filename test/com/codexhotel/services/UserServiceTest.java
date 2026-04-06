package com.codexhotel.services;

import com.codexhotel.data.enums.Role;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.requests.CreateUserRequest;
import com.codexhotel.dtos.responses.UserResponse;
import com.codexhotel.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testThatUserIsCreatedSuccessfully() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");
        request.setRole(Role.GUEST);

        UserResponse response = userService.createUser(request);

        assertEquals("Oluwaseun", response.getName());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testThatCreateUserWithDuplicateEmailThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");

        userService.createUser(request);

        CreateUserRequest duplicate = new CreateUserRequest();
        duplicate.setName("Adedayo");
        duplicate.setEmail("oluwaseun@gmail.com");
        duplicate.setPhoneNumber("08149587217");

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(duplicate));
    }

    @Test
    public void testThatCreateUserWithInvalidEmailThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("seun.com");
        request.setPhoneNumber("08012345678");

        assertThrows(InvalidEmailException.class, () -> userService.createUser(request));
    }

    @Test
    public void testThatCreateUserWithInvalidPhoneNumberThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("0802");

        assertThrows(InvalidPhoneNumberException.class, () -> userService.createUser(request));
    }

    @Test
    public void testThatUserCanBeGottenById() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");

        UserResponse created = userService.createUser(request);

        UserResponse found = userService.getUserById(created.getId());

        assertEquals(created.getId(), found.getId());
    }

    @Test
    public void testThatGetUserByInvalidIdThrowsException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById("123-456"));
    }

    @Test
    public void testThatUserCanBeGottenByEmail() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");

        userService.createUser(request);

        UserResponse found = userService.getUserByEmail("oluwaseun@gmail.com");

        assertEquals("oluwaseun@gmail.com", found.getEmail());
    }

    @Test
    public void testThatOnlyAdminCanGetAllUsers() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse admin = userService.createUser(adminRequest);

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setName("Oluwaseun");
        userRequest.setEmail("oluwaseun@gmail.com");
        userRequest.setPhoneNumber("08012345678");

        userService.createUser(userRequest);

        List<UserResponse> users = userService.getAllUsers(admin.getId());

        assertEquals(2, users.size());
    }

    @Test
    public void testThatNonAdminCanGetAllUsersThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");

        UserResponse user = userService.createUser(request);

        assertThrows(UnauthorizedActionException.class, () -> userService.getAllUsers(user.getId()));
    }

    @Test
    public void testThatUserCanUpdateSelfProfile() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");

        UserResponse user = userService.createUser(request);

        CreateUserRequest update = new CreateUserRequest();
        update.setName("Adedayo");
        update.setEmail("adedayo@gmail.com");
        update.setPhoneNumber("08087654321");

        UserResponse updated = userService.updateUser(user.getId(), update, user.getId());

        assertEquals("Adedayo", updated.getName());
        assertEquals("adedayo@gmail.com", updated.getEmail());
    }

    @Test
    public void testThatAdminCanUpdateUserProfile() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse admin = userService.createUser(adminRequest);

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setName("Oluwaseun");
        userRequest.setEmail("oluwaseun@gmail.com");
        userRequest.setPhoneNumber("08012345678");

        UserResponse user = userService.createUser(userRequest);

        CreateUserRequest update = new CreateUserRequest();
        update.setName("Muyiwa");
        update.setEmail("muyiwa@gmail.com");
        update.setPhoneNumber("08033333333");

        UserResponse updated = userService.updateUser(user.getId(), update, admin.getId());

        assertEquals("Muyiwa", updated.getName());
    }

    @Test
    public void testThatNonAdminUpdatingAnotherUserThrowsException() {
        CreateUserRequest requestOne = new CreateUserRequest();
        requestOne.setName("Oluwaseun");
        requestOne.setEmail("oluwaseun@gmail.com");
        requestOne.setPhoneNumber("08012345678");

        UserResponse userOne = userService.createUser(requestOne);

        CreateUserRequest requestTwo = new CreateUserRequest();
        requestTwo.setName("Adedayo");
        requestTwo.setEmail("adedayo@gmail.com");
        requestTwo.setPhoneNumber("08022222222");

        UserResponse userTwo = userService.createUser(requestTwo);

        CreateUserRequest update = new CreateUserRequest();
        update.setName("Muyiwa");
        update.setEmail("muyiwa@gmail.com");
        update.setPhoneNumber("08033333333");

        assertThrows(UnauthorizedActionException.class, () -> userService.updateUser(userTwo.getId(), update, userOne.getId()));
    }

    @Test
    public void testThatUserCanDeleteSelfProfile() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");

        UserResponse user = userService.createUser(request);

        userService.deleteUser(user.getId(), user.getId());

        assertEquals(0, userRepository.count());
    }

    @Test
    public void testThatAdminCanDeleteUserProfile() {
        CreateUserRequest adminRequest = new CreateUserRequest();
        adminRequest.setName("Madam Bolu");
        adminRequest.setEmail("bolu@gmail.com");
        adminRequest.setPhoneNumber("08033297106");
        adminRequest.setRole(Role.ADMIN);

        UserResponse admin = userService.createUser(adminRequest);

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setName("Oluwaseun");
        userRequest.setEmail("oluwaseun@gmail.com");
        userRequest.setPhoneNumber("08012345678");

        UserResponse user = userService.createUser(userRequest);

        userService.deleteUser(user.getId(), admin.getId());

        assertEquals(1, userRepository.count());
    }

    @Test
    public void testThatNonAdminCanDeleteUserProfileThrowsException() {
        CreateUserRequest requestOne = new CreateUserRequest();
        requestOne.setName("Oluwaseun");
        requestOne.setEmail("oluwaseun@gmail.com");
        requestOne.setPhoneNumber("08012345678");

        UserResponse userOne = userService.createUser(requestOne);

        CreateUserRequest requestTwo = new CreateUserRequest();
        requestTwo.setName("Adedayo");
        requestTwo.setEmail("adedayo@gmail.com");
        requestTwo.setPhoneNumber("08022222222");

        UserResponse userTwo = userService.createUser(requestTwo);

        assertThrows(UnauthorizedActionException.class, () -> userService.deleteUser(userTwo.getId(), userOne.getId()));
    }

    @Test
    public void testThatAdminIsTrue() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Madam Bolu");
        request.setEmail("bolu@gmail.com");
        request.setPhoneNumber("08033297106");
        request.setRole(Role.ADMIN);

        UserResponse admin = userService.createUser(request);

        assertTrue(userService.isAdmin(admin.getId()));
    }

    @Test
    public void testThatAdminIsFalse() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");

        UserResponse user = userService.createUser(request);

        assertFalse(userService.isAdmin(user.getId()));
    }
}