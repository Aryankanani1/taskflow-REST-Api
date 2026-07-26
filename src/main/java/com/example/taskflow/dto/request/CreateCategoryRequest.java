package com.example.taskflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequest {

    @NotBlank(message = "required category name")
    @Size(max = 50, message = "name must be at most 50 characters")
    private String name;

    @Pattern(
            regexp = "^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$",
            message = "color must be a hex code like #FF5733 or #F53"
    )
    private String color;

}
