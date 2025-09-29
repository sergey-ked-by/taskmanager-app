package com.example.taskmanager.dto;

import com.example.taskmanager.model.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private Role role;
    private boolean enabled;
} 