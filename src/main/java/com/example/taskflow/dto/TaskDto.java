package com.example.taskflow.dto;

import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskDto {
private Long id;
private String title;
private String description;
private Priority priority;
private TaskStatus status;
private LocalDate dueDate;
private LocalDateTime createdAt;
private LocalDateTime completedAt;
private CategoryDto category;

}

