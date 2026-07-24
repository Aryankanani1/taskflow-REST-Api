package com.example.taskflow.service.user;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

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
    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresentOrElse(userRepository::delete,() ->{
           throw new UserNotFoundException("user not found");
       });
    }
    @Override
    @Transactional
    public User updateUser(UpdateUserRequest request, Long userId) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    // Partial update: only touch fields the client actually sent a value for.
                    // hasText (not just != null) so an empty/blank string can't wipe a field.
                    if (StringUtils.hasText(request.getName())) {
                        existingUser.setUsername(request.getName());
                    }
                    if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(existingUser.getEmail())) {
                        if (userRepository.existsByEmail(request.getEmail())) {
                            throw new UserAlreadyExistsException("email already in use: " + request.getEmail());
                        }
                        existingUser.setEmail(request.getEmail());
                    }
                    if (StringUtils.hasText(request.getPassword())) {
                        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
                    }
                    return userRepository.save(existingUser);
                }).orElseThrow(() ->
                        new UserNotFoundException("with provided userId user doesn't exists"));
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
