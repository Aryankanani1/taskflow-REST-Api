package com.example.taskflow.service.task;

import com.example.taskflow.dto.TaskDto;
import com.example.taskflow.dto.request.CreateTaskRequest;
import com.example.taskflow.dto.request.UpdateTaskRequest;
import com.example.taskflow.dto.response.PagedResponse;
import com.example.taskflow.entity.Category;
import com.example.taskflow.entity.Task;
import com.example.taskflow.entity.User;
import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.TaskStatus;
import com.example.taskflow.exception.CategoryNotFoundException;
import com.example.taskflow.exception.TaskNotFoundException;
import com.example.taskflow.repository.CategoryRepository;
import com.example.taskflow.repository.TaskRepository;
import com.example.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskServiceInterface {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    // Fields a client is allowed to sort by. Anything else falls back to a safe
    // default, so a bad sortBy can't leak internals or trigger a 500.
    private static final Set<String> SORTABLE =
            Set.of("createdAt", "updatedAt", "dueDate", "priority", "status", "title", "id");
    private static final String DEFAULT_SORT = "createdAt";

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TaskDto> getAll(Long userId, TaskStatus status, Priority priority, Long categoryId,
                                         int page, int size, String sortBy, String sortDir) {
        User owner = userRepository.getReferenceById(userId);
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);

        // Option A: one derived query per filter combination. Check the most-specific
        // combination (all filters) first and fall through to less-specific; findByUser
        // is the no-filter catch-all. Order matters — a broader branch placed first would
        // swallow requests that also carry the narrower filters and silently drop them.
        // Exactly one query runs per request. Every filter is user-scoped, so an unowned
        // categoryId simply yields an empty page rather than leaking another user's tasks.
        // NOTE: 3 optional filters is already 2^3 = 8 branches; a 4th doubles it again —
        // that's the cue to switch to JPA Specifications (Option B).
        Page<Task> result;
        if (status != null && priority != null && categoryId != null) {
            result = taskRepository.findByUserAndStatusAndPriorityAndCategory_Id(owner, status, priority, categoryId, pageable);
        } else if (status != null && priority != null) {
            result = taskRepository.findByUserAndStatusAndPriority(owner, status, priority, pageable);
        } else if (status != null && categoryId != null) {
            result = taskRepository.findByUserAndStatusAndCategory_Id(owner, status, categoryId, pageable);
        } else if (priority != null && categoryId != null) {
            result = taskRepository.findByUserAndPriorityAndCategory_Id(owner, priority, categoryId, pageable);
        } else if (status != null) {
            result = taskRepository.findByUserAndStatus(owner, status, pageable);
        } else if (priority != null) {
            result = taskRepository.findByUserAndPriority(owner, priority, pageable);
        } else if (categoryId != null) {
            result = taskRepository.findByUserAndCategory_Id(owner, categoryId, pageable);
        } else {
            result = taskRepository.findByUser(owner, pageable);
        }

        return PagedResponse.from(result.map(this::toDto));
    }

    // Build a 0-based, clamped, safely-sorted Pageable. page=0 is the first page
    // (matches JPA and the response); size is clamped to 1..100 and sortBy to an
    // allowlist so bad params can't throw, scan the whole table, or leak internals.
    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.min(Math.max(size, 1), 100);
        String sortField = SORTABLE.contains(sortBy) ? sortBy : DEFAULT_SORT;
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDir).orElse(Sort.Direction.ASC);
        return PageRequest.of(pageIndex, pageSize, Sort.by(direction, sortField));
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
