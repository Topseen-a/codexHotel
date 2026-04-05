package com.codexhotel.mapper;

import com.codexhotel.data.models.User;
import com.codexhotel.dtos.requests.CreateUserRequest;
import com.codexhotel.dtos.responses.UserResponse;

public class UserMapper {

    public static User toUser(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        return user;
    }

    public static UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        user.setPhoneNumber(user.getPhoneNumber());
        user.setRole(user.getRole());

        return response;
    }
}
