package com.codexhotel.dtos.requests;

import lombok.Data;

@Data
public class CreateUserRequest {

    private String name;
    private String email;
    private String phoneNumber;
}
