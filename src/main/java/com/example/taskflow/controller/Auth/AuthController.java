package com.example.taskflow.controller.Auth;

import com.example.taskflow.security.request.LoginRequest;
import com.example.taskflow.response.ApiResponse;
import com.example.taskflow.security.jwt.JwtUtils;
import com.example.taskflow.security.response.JwtResponse;
import com.example.taskflow.security.user.UserPrincipal;
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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        // Verifies the email/password against the stored BCrypt hash via DaoAuthenticationProvider.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtUtils.generateToken(authentication);

        // Principal is our UserPrincipal (see UserDetailsService), so the id is right here.
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        JwtResponse jwtResponse = new JwtResponse(principal.getId(), token);
        return ResponseEntity.ok(ApiResponse.ok("login successful", jwtResponse));
    }
}
