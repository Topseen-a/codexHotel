package com.codexhotel.data.models;

import com.codexhotel.data.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private LocalDate createdAt;
}
