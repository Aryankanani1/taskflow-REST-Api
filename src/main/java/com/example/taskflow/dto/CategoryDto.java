package com.example.taskflow.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryDto {

    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdAt;


}
