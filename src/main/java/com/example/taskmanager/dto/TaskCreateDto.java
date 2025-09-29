package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class TaskCreateDto {
    private String title;
    private String description;
}