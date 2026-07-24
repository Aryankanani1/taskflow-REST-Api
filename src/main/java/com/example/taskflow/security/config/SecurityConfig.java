package com.example.taskflow.security.config;
import com.example.taskflow.security.jwt.AuthTokenFilter;
import com.example.taskflow.security.jwt.JwtEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtEntryPoint jwtEntryPoint;

    // Open endpoints (login/register + API docs) — permitted before the secured
    // matchers are evaluated, so /users/register stays public even though
    // /users/** is secured.
    private static final List<String> PUBLIC_URLS =
            List.of("/api/v1/auth/**", "/api/v1/users/register",
                    "/swagger-ui/**", "/swagger-ui.html", "/v1/api-docs/**");

    // Endpoints that require a valid JWT.
    private static final List<String> SECURED_URLS =
            List.of("/api/v1/tasks/**", "/api/v1/users/**");

    // secure password storage and validation
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // login filterChain
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {
http.csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtEntryPoint))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS.toArray(String[]::new)).permitAll()
                .requestMatchers(SECURED_URLS.toArray(String[]::new)).authenticated()
                .anyRequest().authenticated());
        http.authenticationProvider(daoAuthenticationProvider());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    // the component that actually validate the delegates to provider
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        var authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    @Bean
    public AuthTokenFilter authTokenFilter(){
        return new AuthTokenFilter();
    }

    // main interface for the authentication
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws
            Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
