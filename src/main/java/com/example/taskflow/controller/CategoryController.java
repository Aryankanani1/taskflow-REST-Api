package com.example.taskflow.controller;

import com.example.taskflow.dto.CategoryDto;
import com.example.taskflow.dto.request.CreateCategoryRequest;
import com.example.taskflow.dto.request.UpdateCategoryRequest;
import com.example.taskflow.dto.response.ApiResponse;
import com.example.taskflow.security.user.UserPrincipal;
import com.example.taskflow.service.category.CategoryServiceInterface;
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
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryServiceInterface categoryServiceInterface;

    @Operation(
            summary = "List my categories",
            description = "Returns all categories owned by the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "categories returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
                    description = "Server error")
    })
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAll(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<CategoryDto> categories = categoryServiceInterface.getAll(principal.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Categories fetched successfully", categories));
    }

    @Operation(
            summary = "Get a category by id",
            description = "Returns a single category, only if it belongs to the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "category returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "category not found")
    })
    @GetMapping("/{categoryId}/category")
    public ResponseEntity<ApiResponse<CategoryDto>> getById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long categoryId
    ) {
        CategoryDto category = categoryServiceInterface.getById(categoryId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Category fetched successfully", category));
    }

    @Operation(
            summary = "Create a category",
            description = "Creates a category owned by the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
                    description = "category created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "invalid dataset"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
                    description = "category name already exists")
    })
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CategoryDto>> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        CategoryDto category = categoryServiceInterface.create(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Category created successfully", category));
    }

    @Operation(
            summary = "Update a category",
            description = "Partially updates a category, only if it belongs to the authenticated user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "category updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
                    description = "invalid dataset"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "category not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
                    description = "category name already exists")
    })
    @PutMapping("/{categoryId}/update")
    public ResponseEntity<ApiResponse<CategoryDto>> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        CategoryDto category = categoryServiceInterface.update(categoryId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Category updated successfully", category));
    }

    @Operation(
            summary = "Delete a category",
            description = "Deletes a category, only if it belongs to the authenticated user. "
                    + "Tasks in the category are kept but become uncategorized."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "category deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401",
                    description = "not authenticated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "category not found")
    })
    @DeleteMapping("/{categoryId}/delete")
    public ResponseEntity<ApiResponse<Object>> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long categoryId
    ) {
        categoryServiceInterface.delete(categoryId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Category deleted successfully", null));
    }
}
