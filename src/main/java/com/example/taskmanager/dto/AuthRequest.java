package com.example.taskmanager.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthRequest {
    private String login;
    private String password;
}