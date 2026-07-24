package com.example.taskflow.controller;

import com.example.taskflow.dto.TaskDto;
import com.example.taskflow.dto.request.CreateTaskRequest;
import com.example.taskflow.dto.request.UpdateTaskRequest;
import com.example.taskflow.dto.response.ApiResponse;
import com.example.taskflow.enums.TaskStatus;
import com.example.taskflow.security.user.UserPrincipal;
import com.example.taskflow.service.task.TaskServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskServiceInterface taskServiceInterface;

    @Operation(
            summary = "List my tasks",
            description = "Returns all tasks owned by the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "tasks returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
                    description = "Server error")
    })
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getAll(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<TaskDto> tasks = taskServiceInterface.getAll(principal.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Tasks fetched successfully", tasks));
    }

    @Operation(
            summary = "Get a task by id",
            description = "Returns a single task, only if it belongs to the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "task returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "task not found")
    })
    @GetMapping("/{taskId}/task")
    public ResponseEntity<ApiResponse<TaskDto>> getById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId
    ) {
        TaskDto task = taskServiceInterface.getById(taskId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Task fetched successfully", task));
    }

    @Operation(
            summary = "Create a task",
            description = "Creates a task owned by the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
                    description = "task created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "invalid dataset"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "category not found")
    })
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TaskDto>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        TaskDto task = taskServiceInterface.create(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Task created successfully", task));
    }

    @Operation(
            summary = "Update a task",
            description = "Partially updates a task, only if it belongs to the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "task updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "invalid dataset"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "task or category not found")
    })
    @PutMapping("/{taskId}/update")
    public ResponseEntity<ApiResponse<TaskDto>> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        TaskDto task = taskServiceInterface.update(taskId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Task updated successfully", task));
    }

    @Operation(
            summary = "Update a task's status",
            description = "Changes only the status of a task, only if it belongs to the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "status updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "invalid status value"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "task not found")
    })
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskDto>> patchStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId,
            @RequestParam TaskStatus status
    ) {
        TaskDto task = taskServiceInterface.patchStatus(taskId, status, principal.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Task status updated successfully", task));
    }

    @Operation(
            summary = "Delete a task",
            description = "Deletes a task, only if it belongs to the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "task deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "task not found")
    })
    @DeleteMapping("/{taskId}/delete")
    public ResponseEntity<ApiResponse<Object>> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId
    ) {
        taskServiceInterface.delete(taskId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Task deleted successfully", null));
    }
}
