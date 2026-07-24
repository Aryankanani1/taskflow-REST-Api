package com.example.taskflow.dto.request;

import com.example.taskflow.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {

  @NotBlank(message = "title is required")
  @Size(max = 200, message = "title must be at most 200 characters")
  private String title;


  @Size(max = 1000, message = "description must be at most 1000 characters")
  private String description;

  private Priority priority;
  private LocalDate dueDate;
  private Long categoryId;

}
