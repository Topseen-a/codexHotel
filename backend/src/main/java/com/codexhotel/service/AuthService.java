package com.codexhotel.service;

import com.codexhotel.dto.request.CreateUserRequest;
import com.codexhotel.dto.request.LoginRequest;
import com.codexhotel.dto.response.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface AuthService {

    AuthResponse register(@Valid CreateUserRequest request);

    AuthResponse login(@Valid LoginRequest request);
}
