package com.example.taskflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "username is required")
   private String username;
   @Email
   @NotBlank(message = "provide valid email")
   private String email;
   @NotBlank
   @Size(min = 8,max = 64,message = "password must be between 8 and 64 characters")
   private String password;


}
