package com.example.taskmanager.dto;

import lombok.Data;
import lombok.AllArgsConstructor;


@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role;
}