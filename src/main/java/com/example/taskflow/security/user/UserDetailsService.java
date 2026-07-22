package com.example.taskflow.security.user;

import com.example.taskflow.entity.User;
import com.example.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Throw Spring's UsernameNotFoundException (not our own) so DaoAuthenticationProvider
        // folds "unknown email" and "wrong password" into the same BadCredentialsException.
        // That prevents account enumeration via differing login errors.
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("invalid email or password"));
        return UserPrincipal.build(user);
    }
}


