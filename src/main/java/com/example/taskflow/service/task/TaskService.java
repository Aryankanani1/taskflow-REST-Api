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
import com.example.taskflow.repository.spec.TaskSpecifications;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
                                         String search, int page, int size, String sortBy, String sortDir) {
        User owner = userRepository.getReferenceById(userId);
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);

        // Option B: compose one Specification per supplied filter and AND them together.
        // Each dimension is a single predicate rather than its own if-branch, so adding a
        // filter no longer doubles the branch count. Every filter is user-scoped via
        // ownedBy, so an unowned categoryId simply yields an empty page rather than
        // leaking another user's tasks.
        Specification<Task> spec = TaskSpecifications.ownedBy(owner);
        if (status != null) {
            spec = spec.and(TaskSpecifications.hasStatus(status));
        }
        if (priority != null) {
            spec = spec.and(TaskSpecifications.hasPriority(priority));
        }
        if (categoryId != null) {
            spec = spec.and(TaskSpecifications.inCategory(categoryId));
        }
        if (StringUtils.hasText(search)) {
            spec = spec.and(TaskSpecifications.titleContains(search.trim()));
        }

        Page<Task> result = taskRepository.findAll(spec, pageable);
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
