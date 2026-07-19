package com.example.taskflow.service;

import com.example.taskflow.dto.UserDto;
import com.example.taskflow.entity.User;
import com.example.taskflow.exception.UserAlreadyExistsException;
import com.example.taskflow.exception.UserNotFoundException;
import com.example.taskflow.repository.UserRepository;
import com.example.taskflow.request.RegisterRequest;
import com.example.taskflow.request.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public User createUser(RegisterRequest request) {
       return Optional.of(request).filter(user -> !userRepository.existsByEmail(request.getEmail()))
               .map(req -> {
                   User user = new User();
                   user.setUsername(request.getUsername());
                   user.setEmail(request.getEmail());
                   user.setPassword(passwordEncoder.encode(request.getPassword()));
                   return userRepository.save(user);
               }).orElseThrow(() ->
                       new UserAlreadyExistsException("user already exists with this email"));
    }

    @Override
    public void deleteUser() {

    }

    @Override
    public User updateUser(UpdateUserRequest user) {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("user doesn't exists"));
    }

    @Override
    public UserDto convertUserToUserDto(User user) {
        return modelMapper.map(user,UserDto.class);
    }
}
