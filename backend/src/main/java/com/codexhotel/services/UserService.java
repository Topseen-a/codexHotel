package com.codexhotel.services;

import com.codexhotel.data.enums.Role;
import com.codexhotel.data.models.User;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.requests.CreateUserRequest;
import com.codexhotel.dtos.responses.UserResponse;
import com.codexhotel.exceptions.*;
import com.codexhotel.mapper.UserMapper;
import com.codexhotel.notifications.NotificationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NotificationManager notificationManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * Public self-registration. Always creates a GUEST account, regardless of
     * whatever role value is present on the request, so a caller can never
     * grant themselves elevated privileges through this endpoint.
     */
    public UserResponse register(CreateUserRequest request) {
        return createUser(request, Role.GUEST);
    }

    /**
     * Admin-only staff/guest creation. The caller's requested role is honoured here,
     * because access to this method is already gated to ADMIN by the controller
     * and re-checked below.
     */
    public UserResponse createStaffUser(CreateUserRequest request, String callerUserId) {
        User caller = userRepository.findById(callerUserId)
                .orElseThrow(() -> new UserNotFoundException("Caller not found"));

        if (caller.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("Only admins can create staff accounts");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.GUEST;
        return createUser(request, role);
    }

    private UserResponse createUser(CreateUserRequest request, Role role) {
        validateUserRequest(request);

        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = UserMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setCreatedAt(LocalDate.now());

        User savedUser = userRepository.save(user);

        notificationManager.notifyByEmailAndSms(savedUser.getEmail(), savedUser.getPhoneNumber(),
                "Your account has been created with role: " + savedUser.getRole());

        return UserMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return UserMapper.toResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return UserMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers(String requesterId) {
        checkAdmin(requesterId);

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public UserResponse updateUser(String userIdToUpdate, CreateUserRequest request, String callerUserId) {
        User caller = userRepository.findById(callerUserId)
                .orElseThrow(() -> new UserNotFoundException("Caller not found"));

        User userToUpdate = userRepository.findById(userIdToUpdate)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (caller.getRole() != Role.ADMIN && !caller.getId().equals(userToUpdate.getId())) {
            throw new UnauthorizedActionException("You cannot update other users");
        }

        validateUserRequest(request);

        String newEmail = request.getEmail().trim();
        if (!userToUpdate.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        userToUpdate.setName(request.getName());
        userToUpdate.setEmail(newEmail);
        userToUpdate.setPhoneNumber(request.getPhoneNumber());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            userToUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (caller.getRole() == Role.ADMIN && request.getRole() != null) {
            userToUpdate.setRole(request.getRole());
        }

        User savedUser = userRepository.save(userToUpdate);

        notificationManager.notifyByEmailAndSms(savedUser.getEmail(), savedUser.getPhoneNumber(),
                "Your profile has been updated successfully");

        return UserMapper.toResponse(savedUser);
    }

    public void deleteUser(String userIdToDelete, String callerUserId) {
        User caller = userRepository.findById(callerUserId)
                .orElseThrow(() -> new CallerNotFoundException("Caller not found"));

        User userToDelete = userRepository.findById(userIdToDelete)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (caller.getRole() != Role.ADMIN && !caller.getId().equals(userToDelete.getId())) {
            throw new UnauthorizedActionException("You cannot delete other users");
        }

        userRepository.deleteById(userIdToDelete);

        notificationManager.notifyByEmailAndSms(userToDelete.getEmail(), userToDelete.getPhoneNumber(),
                "Your account has been deleted successfully");
    }

    public boolean isAdmin(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return user.getRole() == Role.ADMIN;
    }

    private void checkAdmin(String userId) {
        if (!isAdmin(userId)) {
            throw new UnauthorizedActionException("Admin privileges required");
        }
    }

    private void validateUserRequest(CreateUserRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new NameCannotBeEmptyException("Name cannot be empty");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new EmailCannotBeEmptyException("Email cannot be empty");
        }
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidEmailException("Email format is invalid");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new PhoneNumberCannotBeEmptyException("Phone number cannot be empty");
        }
        if (!request.getPhoneNumber().matches("^(\\+234|0)[789][01]\\d{8}$")) {
            throw new InvalidPhoneNumberException("Phone number must be a valid Nigerian number");
        }
    }
}
