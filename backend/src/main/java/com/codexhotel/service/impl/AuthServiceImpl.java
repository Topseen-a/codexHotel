package com.codexhotel.service.impl;

import com.codexhotel.dto.request.CreateUserRequest;
import com.codexhotel.dto.request.LoginRequest;
import com.codexhotel.dto.response.AuthResponse;
import com.codexhotel.dto.response.UserResponse;
import com.codexhotel.security.AccessGuard;
import com.codexhotel.security.JwtService;
import com.codexhotel.security.UserPrincipal;
import com.codexhotel.service.AuthService;
import com.codexhotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AccessGuard accessGuard;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(CreateUserRequest request) {
        UserResponse createdUser = userService.register(request);
        UserPrincipal principal = new UserPrincipal(accessGuard.requireUser(createdUser.getId()));

        return new AuthResponse(jwtService.generateToken(principal), createdUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UserResponse user = userService.getUserById(principal.getId());

        return new AuthResponse(jwtService.generateToken(principal), user);
    }
}
