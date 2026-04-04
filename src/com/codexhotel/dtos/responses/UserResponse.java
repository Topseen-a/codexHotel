package com.codexhotel.dtos.responses;

import com.codexhotel.data.enums.Role;
import lombok.Data;

@Data
public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
}
