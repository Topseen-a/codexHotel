package com.codexhotel.controllers;

import com.codexhotel.dtos.requests.CreateUserRequest;
import com.codexhotel.dtos.responses.ApiResponse;
import com.codexhotel.dtos.responses.UserResponse;
import com.codexhotel.security.SecurityUtils;
import com.codexhotel.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Admin-only: create a staff or guest account directly (as opposed to
     * public self-registration at /api/auth/register).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request,
                                                                 Authentication authentication) {
        UserResponse response = userService.createStaffUser(request, SecurityUtils.currentUserId(authentication));
        return new ResponseEntity<>(ApiResponse.success("User created", response), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/email")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','RECEPTIONIST')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@RequestParam String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(Authentication authentication) {
        List<UserResponse> response = userService.getAllUsers(SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable String id,
                                                                  @RequestBody CreateUserRequest request,
                                                                  Authentication authentication) {
        UserResponse response = userService.updateUser(id, request, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("User updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id, Authentication authentication) {
        userService.deleteUser(id, SecurityUtils.currentUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}
