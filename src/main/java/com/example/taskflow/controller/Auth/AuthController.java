package com.example.taskflow.controller.Auth;

import com.example.taskflow.entity.User;
import com.example.taskflow.exception.UserNotFoundException;
import com.example.taskflow.repository.UserRepository;
import com.example.taskflow.request.LoginRequest;
import com.example.taskflow.response.ApiResponse;
import com.example.taskflow.security.jwt.JwtUtils;
import com.example.taskflow.security.response.JwtResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        // Verifies the email/password against the stored BCrypt hash via DaoAuthenticationProvider.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtUtils.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("no user found"));

        JwtResponse jwtResponse = new JwtResponse(user.getId(), token);
        return ResponseEntity.ok(ApiResponse.ok("login successful", jwtResponse));
    }
}
