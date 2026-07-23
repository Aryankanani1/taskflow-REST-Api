package com.example.taskflow.dto;

import com.example.taskflow.entity.Task;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    private Long id;
    private String userName;
    private List<TaskDto> tasks;
    private String email;
    private List<CategoryDto> categories;

}
