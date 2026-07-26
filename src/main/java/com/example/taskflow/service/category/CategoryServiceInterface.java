package com.example.taskflow.service.category;

import com.example.taskflow.dto.CategoryDto;
import com.example.taskflow.dto.request.CreateCategoryRequest;
import com.example.taskflow.dto.request.UpdateCategoryRequest;

import java.util.List;

public interface CategoryServiceInterface {

    // All categories owned by the given user.
    List<CategoryDto> getAll(Long userId);

    // The category only if it belongs to the user; otherwise throw (mapped to 404).
    CategoryDto getById(Long categoryId, Long userId);

    // Create a category owned by the user; rejects a duplicate name for that user.
    CategoryDto create(CreateCategoryRequest request, Long userId);

    // Partially update the category only if it belongs to the user.
    CategoryDto update(Long categoryId, UpdateCategoryRequest request, Long userId);

    // Delete the category only if it belongs to the user; its tasks are kept but
    // orphaned (category set to null).
    void delete(Long categoryId, Long userId);

}
