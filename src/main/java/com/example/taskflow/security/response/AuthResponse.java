package com.example.taskflow.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String id;
    private String token;
    private String tokenType;

}
