package com.example.taskflow.service;

import com.example.taskflow.dto.UserDto;
import com.example.taskflow.entity.User;
import com.example.taskflow.request.RegisterRequest;
import com.example.taskflow.request.UpdateUserRequest;

import java.util.Optional;

public interface UserServiceInterface {


   User createUser(RegisterRequest user);
   void deleteUser();
   User updateUser(UpdateUserRequest user);
    User getUserById(Long id);

    UserDto convertUserToUserDto(User user);

}
