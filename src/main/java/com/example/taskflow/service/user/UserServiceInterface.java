package com.example.taskflow.service.user;

import com.example.taskflow.dto.UserDto;
import com.example.taskflow.entity.User;
import com.example.taskflow.dto.request.RegisterRequest;
import com.example.taskflow.dto.request.UpdateUserRequest;

public interface UserServiceInterface {


   User createUser(RegisterRequest user);
   void deleteUser(Long id);
   User updateUser(UpdateUserRequest user,Long userId);
    User getUserById(Long id);

    UserDto convertUserToUserDto(User user);

}
