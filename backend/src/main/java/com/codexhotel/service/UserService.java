package com.codexhotel.service;

import com.codexhotel.dto.request.CreateUserRequest;
import com.codexhotel.dto.request.UpdateUserRequest;
import com.codexhotel.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface UserService {

    UserResponse register(@Valid CreateUserRequest request);

    UserResponse createStaffUser(@Valid CreateUserRequest request, String callerUserId);

    UserResponse getUserById(String id);

    UserResponse getUserById(String id, String requesterId);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getAllUsers(String requesterId);

    UserResponse updateUser(String userIdToUpdate, @Valid UpdateUserRequest request, String callerUserId);

    void deleteUser(String userIdToDelete, String callerUserId);

    boolean isAdmin(String userId);
}
