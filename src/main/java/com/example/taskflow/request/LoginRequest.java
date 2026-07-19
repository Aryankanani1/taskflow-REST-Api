package com.example.taskflow.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

   // login is by email (the unique identity), not username
   @NotBlank(message = "email is required")
   @Email(message = "please provide valid email address")
   private String email;
   @NotBlank(message = "Enter correct password")
   private String password;
}
