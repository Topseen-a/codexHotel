package com.codexhotel.security;

import com.codexhotel.enums.Role;
import com.codexhotel.exception.ForbiddenException;
import com.codexhotel.exception.ResourceNotFoundException;
import com.codexhotel.model.User;
import com.codexhotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AccessGuard {

    private final UserRepository userRepository;

    public User requireUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User requireRole(String userId, String message, Role... allowedRoles) {
        User user = requireUser(userId);
        if (!Set.of(allowedRoles).contains(user.getRole())) {
            throw new ForbiddenException(message);
        }
        return user;
    }

    public User requireStaff(String userId, String message) {
        User user = requireUser(userId);
        if (!user.getRole().isStaff()) {
            throw new ForbiddenException(message);
        }
        return user;
    }

    public User requireOwnerOrStaff(String requesterId, String ownerId, String message) {
        User requester = requireUser(requesterId);
        if (!requester.getRole().isStaff() && !requester.getId().equals(ownerId)) {
            throw new ForbiddenException(message);
        }
        return requester;
    }
}
