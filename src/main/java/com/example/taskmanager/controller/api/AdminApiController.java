package com.example.taskmanager.controller.api;

import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.dto.StatisticsDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.ITaskService;
import com.example.taskmanager.service.IUserService;
import com.example.taskmanager.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin", description = "Admin management APIs")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminApiController {
    
    private final IUserService userService;
    private final ITaskService taskService;
    private final UserMapper userMapper;
    
    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAllUsers().stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/users/{id}/role")
    @Operation(summary = "Update user role")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        User user = userService.updateUserRole(id, role);
        return ResponseEntity.ok(userMapper.toDto(user));
    }
    
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get system statistics")
    public ResponseEntity<StatisticsDto> getStatistics() {
        StatisticsDto stats = new StatisticsDto();
        stats.setTotalUsers(userService.getUserCount());
        stats.setAdminUsers(userService.getAdminCount());
        stats.setRegularUsers(userService.getRegularUserCount());
        stats.setTotalTasks(taskService.findAllTasks().size());
        stats.setPendingTasks(taskService.findTasksByStatus(com.example.taskmanager.model.TaskStatus.PENDING).size());
        stats.setInProgressTasks(taskService.findTasksByStatus(com.example.taskmanager.model.TaskStatus.IN_PROGRESS).size());
        stats.setCompletedTasks(taskService.findTasksByStatus(com.example.taskmanager.model.TaskStatus.COMPLETED).size());
        return ResponseEntity.ok(stats);
    }
}