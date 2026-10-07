package com.codexhotel.mapper;

import com.codexhotel.model.User;
import com.codexhotel.dto.request.CreateUserRequest;
import com.codexhotel.dto.response.UserResponse;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toUser(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());
        return user;
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }
}
