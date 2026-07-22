package com.example.taskflow.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    // All fields optional (partial update). Constraints only fire when a value is present.
    private String name;

    @Email(message = "please provide a valid email address")
    private String email;

    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;
}
