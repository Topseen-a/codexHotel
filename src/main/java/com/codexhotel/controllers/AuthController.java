package com.codexhotel.controllers;

import com.codexhotel.data.models.User;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.requests.CreateUserRequest;
import com.codexhotel.dtos.requests.LoginRequest;
import com.codexhotel.dtos.responses.ApiResponse;
import com.codexhotel.dtos.responses.AuthResponse;
import com.codexhotel.dtos.responses.UserResponse;
import com.codexhotel.exceptions.InvalidCredentialsException;
import com.codexhotel.security.JwtService;
import com.codexhotel.security.UserPrincipal;
import com.codexhotel.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody CreateUserRequest request) {
        UserResponse createdUser = userService.register(request);

        User user = userRepository.findById(createdUser.getId()).orElseThrow();
        String token = jwtService.generateToken(new UserPrincipal(user));

        AuthResponse response = new AuthResponse(token, createdUser);
        return new ResponseEntity<>(ApiResponse.success("Account created", response), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtService.generateToken(principal);

            UserResponse userResponse = userService.getUserById(principal.getId());

            return ResponseEntity.ok(ApiResponse.success("Login successful", new AuthResponse(token, userResponse)));
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UserResponse response = userService.getUserById(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
