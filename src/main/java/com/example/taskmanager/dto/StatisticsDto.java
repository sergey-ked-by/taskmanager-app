package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class StatisticsDto {
    private long totalUsers;
    private long adminUsers;
    private long regularUsers;
    private long totalTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long completedTasks;
} 