package com.example.taskflow.service.task;

import com.example.taskflow.dto.TaskDto;
import com.example.taskflow.dto.request.CreateTaskRequest;
import com.example.taskflow.dto.request.UpdateTaskRequest;
import com.example.taskflow.entity.Category;
import com.example.taskflow.entity.Task;
import com.example.taskflow.entity.User;
import com.example.taskflow.enums.TaskStatus;
import com.example.taskflow.exception.CategoryNotFoundException;
import com.example.taskflow.exception.TaskNotFoundException;
import com.example.taskflow.repository.CategoryRepository;
import com.example.taskflow.repository.TaskRepository;
import com.example.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskServiceInterface {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getAll(Long userId) {
        User owner = userRepository.getReferenceById(userId);
        return taskRepository.findByUser(owner).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getById(Long taskId, Long userId) {
        return toDto(requireOwnedTask(taskId, userId));
    }

    @Override
    @Transactional
    public TaskDto create(CreateTaskRequest request, Long userId) {
        User owner = userRepository.getReferenceById(userId);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        task.setDueDate(request.getDueDate());
        task.setUser(owner);
        if (request.getCategoryId() != null) {
            task.setCategory(requireOwnedCategory(request.getCategoryId(), owner));
        }

        return toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDto update(Long taskId, UpdateTaskRequest request, Long userId) {
        User owner = userRepository.getReferenceById(userId);
        Task task = requireOwnedTask(taskId, userId);

        // Partial update: only touch fields the client actually sent a value for.
        if (StringUtils.hasText(request.getTitle())) {
            task.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getDescription())) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getCategoryId() != null) {
            task.setCategory(requireOwnedCategory(request.getCategoryId(), owner));
        }

        return toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDto patchStatus(Long taskId, TaskStatus status, Long userId) {
        Task task = requireOwnedTask(taskId, userId);
        task.setStatus(status);
        return toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void delete(Long taskId, Long userId) {
        taskRepository.delete(requireOwnedTask(taskId, userId));
    }

    // Fetch a task scoped to its owner so users can only reach their own; 404 otherwise.
    private Task requireOwnedTask(Long taskId, Long userId) {
        User owner = userRepository.getReferenceById(userId);
        return taskRepository.findByIdAndUser(taskId, owner)
                .orElseThrow(() -> new TaskNotFoundException("task not found with id: " + taskId));
    }

    // A user may only attach their own categories to a task.
    private Category requireOwnedCategory(Long categoryId, User owner) {
        return categoryRepository.findByIdAndUser(categoryId, owner)
                .orElseThrow(() -> new CategoryNotFoundException("category not found with id: " + categoryId));
    }

    private TaskDto toDto(Task task) {
        return modelMapper.map(task, TaskDto.class);
    }
}
