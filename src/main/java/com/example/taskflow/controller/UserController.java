
package com.example.taskflow.controller;

import com.example.taskflow.dto.UserDto;
import com.example.taskflow.entity.User;
import com.example.taskflow.request.RegisterRequest;
import com.example.taskflow.response.ApiResponse;
import com.example.taskflow.service.UserServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceInterface userServiceInterface;

    @Operation(
            summary = "Register User",
            description = "Register User with username, email, password"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
                    description = "user created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
                    description = "Server error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
            description = "invalid dataset"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
                    description = "resource already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@Valid @RequestBody RegisterRequest request) {
        User user = userServiceInterface.createUser(request);
        UserDto userDto = userServiceInterface.convertUserToUserDto(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User registered successfully", userDto));
    }
}
