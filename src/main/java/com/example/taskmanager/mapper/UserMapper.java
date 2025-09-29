package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        return dto;
    }
} 