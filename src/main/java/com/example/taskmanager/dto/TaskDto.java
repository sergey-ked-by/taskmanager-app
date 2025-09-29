package com.example.taskmanager.dto;

import com.example.taskmanager.model.TaskStatus;
import lombok.Data;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long userId;
} 