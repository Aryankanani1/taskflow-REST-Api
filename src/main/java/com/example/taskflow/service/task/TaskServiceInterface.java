package com.example.taskflow.service.task;

import com.example.taskflow.dto.TaskDto;
import com.example.taskflow.dto.request.CreateTaskRequest;
import com.example.taskflow.dto.request.UpdateTaskRequest;
import com.example.taskflow.dto.response.PagedResponse;
import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.TaskStatus;

import java.util.List;

public interface TaskServiceInterface {

    // A page of the user's tasks, optionally filtered by any combination of status,
    // priority, and category (null = filter not applied). page is 0-based.
    PagedResponse<TaskDto> getAll(Long userId, TaskStatus status, Priority priority, Long categoryId,
                                  int page, int size, String sortBy, String sortDir);

    // The task only if it belongs to the user; otherwise throw (mapped to 404).
    TaskDto getById(Long taskId, Long userId);

    // Create a task with the given user as its owner.
    TaskDto create(CreateTaskRequest request, Long userId);

    // Update the task only if it belongs to the user.
    TaskDto update(Long taskId, UpdateTaskRequest request, Long userId);

    // Update only the status, only if the task belongs to the user.
    TaskDto patchStatus(Long taskId, TaskStatus status, Long userId);

    // Delete the task only if it belongs to the user.
    void delete(Long taskId, Long userId);

}
