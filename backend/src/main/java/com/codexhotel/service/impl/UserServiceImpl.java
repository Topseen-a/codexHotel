package com.codexhotel.service.impl;

import com.codexhotel.dto.request.CreateUserRequest;
import com.codexhotel.dto.request.UpdateUserRequest;
import com.codexhotel.dto.response.UserResponse;
import com.codexhotel.enums.Role;
import com.codexhotel.exception.ConflictException;
import com.codexhotel.exception.ForbiddenException;
import com.codexhotel.exception.ResourceNotFoundException;
import com.codexhotel.mapper.UserMapper;
import com.codexhotel.model.User;
import com.codexhotel.notification.NotificationManager;
import com.codexhotel.repository.UserRepository;
import com.codexhotel.security.AccessGuard;
import com.codexhotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccessGuard accessGuard;
    private final NotificationManager notificationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(CreateUserRequest request) {
        return createUser(request, Role.GUEST);
    }

    @Override
    public UserResponse createStaffUser(CreateUserRequest request, String callerUserId) {
        accessGuard.requireRole(callerUserId, "Only admins can create staff accounts", Role.ADMIN);

        Role role = request.getRole() != null ? request.getRole() : Role.GUEST;
        return createUser(request, role);
    }

    @Override
    public UserResponse getUserById(String id) {
        return UserMapper.toResponse(accessGuard.requireUser(id));
    }

    @Override
    public UserResponse getUserById(String id, String requesterId) {
        accessGuard.requireOwnerOrStaff(requesterId, id, "You are not allowed to view this user");
        return getUserById(id);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers(String requesterId) {
        accessGuard.requireRole(requesterId, "Admin privileges required", Role.ADMIN);

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse updateUser(String userIdToUpdate, UpdateUserRequest request, String callerUserId) {
        User caller = accessGuard.requireUser(callerUserId);
        User userToUpdate = accessGuard.requireUser(userIdToUpdate);

        if (caller.getRole() != Role.ADMIN && !caller.getId().equals(userToUpdate.getId())) {
            throw new ForbiddenException("You cannot update other users");
        }

        String newEmail = request.getEmail().trim();
        if (!userToUpdate.getEmail().equals(newEmail)) {
            ensureEmailIsAvailable(newEmail);
        }

        userToUpdate.setName(request.getName().trim());
        userToUpdate.setEmail(newEmail);
        userToUpdate.setPhoneNumber(request.getPhoneNumber().trim());

        if (request.getPassword() != null) {
            userToUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
            userToUpdate.setPasswordChangedAt(Instant.now());
        }

        if (caller.getRole() == Role.ADMIN && request.getRole() != null) {
            userToUpdate.setRole(request.getRole());
        }

        User savedUser = saveUser(userToUpdate);

        notificationManager.notifyByEmailAndSms(savedUser.getEmail(), savedUser.getPhoneNumber(),
                "Your profile has been updated successfully");

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public void deleteUser(String userIdToDelete, String callerUserId) {
        User caller = accessGuard.requireUser(callerUserId);
        User userToDelete = accessGuard.requireUser(userIdToDelete);

        if (caller.getRole() != Role.ADMIN && !caller.getId().equals(userToDelete.getId())) {
            throw new ForbiddenException("You cannot delete other users");
        }

        userRepository.delete(userToDelete);

        notificationManager.notifyByEmailAndSms(userToDelete.getEmail(), userToDelete.getPhoneNumber(),
                "Your account has been deleted successfully");
    }

    @Override
    public boolean isAdmin(String userId) {
        return accessGuard.requireUser(userId).getRole() == Role.ADMIN;
    }

    private UserResponse createUser(CreateUserRequest request, Role role) {
        User user = UserMapper.toUser(request);
        ensureEmailIsAvailable(user.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setCreatedAt(LocalDate.now());

        User savedUser = saveUser(user);

        notificationManager.notifyByEmailAndSms(savedUser.getEmail(), savedUser.getPhoneNumber(),
                "Your account has been created with role: " + savedUser.getRole());

        return UserMapper.toResponse(savedUser);
    }

    /** Saves the user, translating a unique-index race on email into a 409. */
    private User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DuplicateKeyException ex) {
            throw new ConflictException("Email already exists");
        }
    }

    private void ensureEmailIsAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already exists");
        }
    }
}
