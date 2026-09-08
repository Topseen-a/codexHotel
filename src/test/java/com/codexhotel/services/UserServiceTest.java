package com.codexhotel.services;

import com.codexhotel.data.enums.Role;
import com.codexhotel.data.models.User;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.requests.CreateUserRequest;
import com.codexhotel.dtos.responses.UserResponse;
import com.codexhotel.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
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

    /** Directly persists a fixture user with a given role, bypassing the service layer. */
    private User seedUser(String name, String email, String phone, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setPassword("$2a$10$notARealHashJustForFixtures");
        user.setRole(role);
        user.setCreatedAt(LocalDate.now());
        return userRepository.save(user);
    }

    @Test
    public void testThatUserIsCreatedSuccessfully() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");
        request.setPassword("password123");

        UserResponse response = userService.register(request);

        assertEquals("Oluwaseun", response.getName());
        assertEquals(Role.GUEST, response.getRole());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testThatRegisterIgnoresRequestedRoleAndForcesGuest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");
        request.setPassword("password123");
        request.setRole(Role.ADMIN);

        UserResponse response = userService.register(request);

        assertEquals(Role.GUEST, response.getRole());
    }

    @Test
    public void testThatCreateUserWithDuplicateEmailThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");
        request.setPassword("password123");

        userService.register(request);

        CreateUserRequest duplicate = new CreateUserRequest();
        duplicate.setName("Adedayo");
        duplicate.setEmail("oluwaseun@gmail.com");
        duplicate.setPhoneNumber("08149587217");
        duplicate.setPassword("password123");

        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(duplicate));
    }

    @Test
    public void testThatCreateUserWithInvalidEmailThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("seun.com");
        request.setPhoneNumber("08012345678");
        request.setPassword("password123");

        assertThrows(InvalidEmailException.class, () -> userService.register(request));
    }

    @Test
    public void testThatCreateUserWithInvalidPhoneNumberThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("0802");
        request.setPassword("password123");

        assertThrows(InvalidPhoneNumberException.class, () -> userService.register(request));
    }

    @Test
    public void testThatCreateUserWithShortPasswordThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");
        request.setPassword("short");

        assertThrows(InvalidPasswordException.class, () -> userService.register(request));
    }

    @Test
    public void testThatUserCanBeGottenById() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPhoneNumber("08012345678");
        request.setPassword("password123");

        UserResponse created = userService.register(request);

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
        request.setPassword("password123");

        userService.register(request);

        UserResponse found = userService.getUserByEmail("oluwaseun@gmail.com");

        assertEquals("oluwaseun@gmail.com", found.getEmail());
    }

    @Test
    public void testThatOnlyAdminCanGetAllUsers() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);
        seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);

        List<UserResponse> users = userService.getAllUsers(admin.getId());

        assertEquals(2, users.size());
    }

    @Test
    public void testThatNonAdminCanGetAllUsersThrowsException() {
        User user = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);

        assertThrows(UnauthorizedActionException.class, () -> userService.getAllUsers(user.getId()));
    }

    @Test
    public void testThatUserCanUpdateSelfProfile() {
        User user = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);

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
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);
        User user = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);

        CreateUserRequest update = new CreateUserRequest();
        update.setName("Muyiwa");
        update.setEmail("muyiwa@gmail.com");
        update.setPhoneNumber("08033333333");

        UserResponse updated = userService.updateUser(user.getId(), update, admin.getId());

        assertEquals("Muyiwa", updated.getName());
    }

    @Test
    public void testThatNonAdminUpdatingAnotherUserThrowsException() {
        User userOne = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);
        User userTwo = seedUser("Adedayo", "adedayo@gmail.com", "08022222222", Role.GUEST);

        CreateUserRequest update = new CreateUserRequest();
        update.setName("Muyiwa");
        update.setEmail("muyiwa@gmail.com");
        update.setPhoneNumber("08033333333");

        assertThrows(UnauthorizedActionException.class, () -> userService.updateUser(userTwo.getId(), update, userOne.getId()));
    }

    @Test
    public void testThatUserCanDeleteSelfProfile() {
        User user = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);

        userService.deleteUser(user.getId(), user.getId());

        assertEquals(0, userRepository.count());
    }

    @Test
    public void testThatAdminCanDeleteUserProfile() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);
        User user = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);

        userService.deleteUser(user.getId(), admin.getId());

        assertEquals(1, userRepository.count());
    }

    @Test
    public void testThatNonAdminCanDeleteUserProfileThrowsException() {
        User userOne = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);
        User userTwo = seedUser("Adedayo", "adedayo@gmail.com", "08022222222", Role.GUEST);

        assertThrows(UnauthorizedActionException.class, () -> userService.deleteUser(userTwo.getId(), userOne.getId()));
    }

    @Test
    public void testThatAdminIsTrue() {
        User admin = seedUser("Madam Bolu", "bolu@gmail.com", "08033297106", Role.ADMIN);

        assertTrue(userService.isAdmin(admin.getId()));
    }

    @Test
    public void testThatAdminIsFalse() {
        User user = seedUser("Oluwaseun", "oluwaseun@gmail.com", "08012345678", Role.GUEST);

        assertFalse(userService.isAdmin(user.getId()));
    }
}
