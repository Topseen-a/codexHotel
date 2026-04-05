package com.codexhotel.dtos.requests;

import com.codexhotel.data.enums.Role;
import lombok.Data;

@Data
public class CreateUserRequest {

    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
}
