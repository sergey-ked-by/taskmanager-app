package com.example.taskmanager.dto;

import com.example.taskmanager.model.Task;
import lombok.Data;

@Data
public class TaskUpdateDto {
    private String title;
    private String description;
    private String status;

    public static TaskUpdateDto fromEntity(Task task) {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus().name());
        return dto;
    }
}