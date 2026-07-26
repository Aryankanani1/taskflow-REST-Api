package com.example.taskflow.service.category;

import com.example.taskflow.dto.CategoryDto;
import com.example.taskflow.dto.request.CreateCategoryRequest;
import com.example.taskflow.dto.request.UpdateCategoryRequest;
import com.example.taskflow.entity.Category;
import com.example.taskflow.entity.Task;
import com.example.taskflow.entity.User;
import com.example.taskflow.exception.CategoryAlreadyExistsException;
import com.example.taskflow.exception.CategoryNotFoundException;
import com.example.taskflow.repository.CategoryRepository;
import com.example.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceInterface {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Long userId) {
        User owner = userRepository.getReferenceById(userId);
        return categoryRepository.findByUser(owner).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long categoryId, Long userId) {
        return toDto(requireOwnedCategory(categoryId, userId));
    }

    @Override
    @Transactional
    public CategoryDto create(CreateCategoryRequest request, Long userId) {
        User owner = userRepository.getReferenceById(userId);
        requireNameAvailable(request.getName(), owner);

        Category category = new Category();
        category.setName(request.getName());
        category.setColor(request.getColor());
        category.setUser(owner);

        return toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(Long categoryId, UpdateCategoryRequest request, Long userId) {
        User owner = userRepository.getReferenceById(userId);
        Category category = requireOwnedCategory(categoryId, userId);

        // Partial update: only touch fields the client actually sent a value for.
        if (StringUtils.hasText(request.getName())
                && !request.getName().equals(category.getName())) {
            requireNameAvailable(request.getName(), owner);
            category.setName(request.getName());
        }
        if (StringUtils.hasText(request.getColor())) {
            category.setColor(request.getColor());
        }

        return toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long categoryId, Long userId) {
        Category category = requireOwnedCategory(categoryId, userId);
        // Keep the tasks, just detach them from the category being removed. category_id
        // is nullable, so these updates flush before the delete and avoid a FK violation.
        for (Task task : category.getTasks()) {
            task.setCategory(null);
        }
        categoryRepository.delete(category);
    }

    // Fetch a category scoped to its owner so users can only reach their own; 404 otherwise.
    private Category requireOwnedCategory(Long categoryId, Long userId) {
        User owner = userRepository.getReferenceById(userId);
        return categoryRepository.findByIdAndUser(categoryId, owner)
                .orElseThrow(() -> new CategoryNotFoundException("category not found with id: " + categoryId));
    }

    // Category names are unique per user; reject a name that owner already uses.
    private void requireNameAvailable(String name, User owner) {
        if (categoryRepository.existsByNameAndUser(name, owner)) {
            throw new CategoryAlreadyExistsException("category already exists with name: " + name);
        }
    }

    private CategoryDto toDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }
}
