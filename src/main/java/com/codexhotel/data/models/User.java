package com.codexhotel.data.models;

import com.codexhotel.data.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;

    @Indexed(unique = true)
    private String email;

    private String phoneNumber;

    @JsonIgnore
    private String password;

    private Role role;
    private LocalDate createdAt;
}
