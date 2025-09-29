package com.example.taskmanager.controller.api;

import com.example.taskmanager.dto.TaskCreateDto;
import com.example.taskmanager.dto.TaskUpdateDto;
import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.ITaskService;
import com.example.taskmanager.mapper.TaskMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Tasks", description = "Task management APIs")
@AllArgsConstructor
public class TaskApiController {
    
    private ITaskService taskService;
    
    private TaskMapper taskMapper;
    
    @GetMapping
    @Operation(summary = "Get user's tasks")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> getUserTasks(Authentication authentication) {
        List<TaskDto> tasks = taskService.findUserTasks(authentication.getName()).stream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all tasks (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.findAllTasks().stream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        Task task = taskService.findTaskById(id);
        return ResponseEntity.ok(taskMapper.toDto(task));
    }
    
    @PostMapping
    @Operation(summary = "Create a new task")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskCreateDto taskDto, 
                                         Authentication authentication) {
        Task task = taskService.createTask(taskDto, authentication.getName());
        return new ResponseEntity<>(taskMapper.toDto(task), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, 
                                         @Valid @RequestBody TaskUpdateDto taskDto,
                                         Authentication authentication) {
        Task task = taskService.updateTask(id, taskDto, authentication.getName());
        return ResponseEntity.ok(taskMapper.toDto(task));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        taskService.deleteTask(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable TaskStatus status,
                                                     Authentication authentication) {
        List<TaskDto> tasks = taskService.findUserTasksByStatus(authentication.getName(), status).stream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search tasks by title")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<TaskDto>> searchTasks(@RequestParam String title,
                                                Authentication authentication) {
        List<TaskDto> tasks = taskService.searchUserTasks(authentication.getName(), title).stream()
            .map(taskMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }
}