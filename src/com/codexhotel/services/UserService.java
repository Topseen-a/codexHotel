package com.codexhotel.services;

import com.codexhotel.data.enums.Role;
import com.codexhotel.data.models.User;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.requests.CreateUserRequest;
import com.codexhotel.dtos.responses.UserResponse;
import com.codexhotel.exceptions.*;
import com.codexhotel.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(CreateUserRequest request) {
        validateUserRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = UserMapper.toUser(request);
        user.setRole(Role.GUEST);
        user.setCreatedAt(LocalDate.now());

        User savedUser = userRepository.save(user);
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

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public UserResponse updateUser(String id, CreateUserRequest request) {
        validateUserRequest(request);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newEmail = request.getEmail().trim();

        if (!existingUser.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhoneNumber(request.getPhoneNumber());

        User savedUser = userRepository.save(existingUser);
        return UserMapper.toResponse(savedUser);
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);
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
