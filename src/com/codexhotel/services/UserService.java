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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NotificationManager notificationManager;

    public UserResponse createUser(CreateUserRequest request) {
        validateUserRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = UserMapper.toUser(request);
        user.setRole(request.getRole() != null ? request.getRole() : Role.GUEST);
        user.setCreatedAt(LocalDate.now());

        User savedUser = userRepository.save(user);

        notificationManager.notifyByEmailAndSms(savedUser.getEmail(), savedUser.getPhoneNumber(), "Your account has been created with role: " + savedUser.getRole());

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

    public List<UserResponse> getAllUsers(String adminUserId) {
        checkAdmin(adminUserId);

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public UserResponse updateUser(String userIdToUpdate, CreateUserRequest request, String callerUserId) {
        validateUserRequest(request);

        User caller = userRepository.findById(callerUserId)
                .orElseThrow(() -> new UserNotFoundException("Caller not found"));

        User userToUpdate = userRepository.findById(userIdToUpdate)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (caller.getRole() != Role.ADMIN && !caller.getId().equals(userToUpdate.getId())) {
            throw new UnauthorizedActionException("You cannot update other users");
        }

        String newEmail = request.getEmail().trim();
        if (!userToUpdate.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        userToUpdate.setName(request.getName());
        userToUpdate.setEmail(request.getEmail());
        userToUpdate.setPhoneNumber(request.getPhoneNumber());

        if (caller.getRole() == Role.ADMIN && request.getRole() != null) {
            userToUpdate.setRole(request.getRole());
        }

        User savedUser = userRepository.save(userToUpdate);

        notificationManager.notifyByEmailAndSms(savedUser.getEmail(), savedUser.getPhoneNumber(), "Your profile has been updated successfully");

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

        notificationManager.notifyByEmailAndSms(userToDelete.getEmail(), userToDelete.getPhoneNumber(), "Your account has been deleted successfully");
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