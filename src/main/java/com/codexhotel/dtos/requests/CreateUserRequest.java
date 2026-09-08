package com.codexhotel.dtos.requests;

import com.codexhotel.data.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    /**
     * Only honoured on the admin-only staff-creation endpoint.
     * Public self-registration always creates a GUEST account regardless of this value.
     */
    private Role role;
}
