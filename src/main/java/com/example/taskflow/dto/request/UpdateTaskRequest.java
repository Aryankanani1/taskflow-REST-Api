package com.example.taskflow.dto.request;

import com.example.taskflow.enums.Priority;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {

    // All fields optional (partial update). Constraints only fire when a value is present.
    // Status changes go through the dedicated patchStatus endpoint.
    @Size(max = 200, message = "title must be at most 200 characters")
    private String title;
    @Size(max = 1000, message = "description must be at most 1000 characters")
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private Long categoryId;

}
