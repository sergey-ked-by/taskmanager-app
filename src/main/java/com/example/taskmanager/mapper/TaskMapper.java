package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskDto toDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setUserId(task.getUser().getId());
        return dto;
    }
} 