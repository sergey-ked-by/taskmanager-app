package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskCreateDto;
import com.example.taskmanager.dto.TaskUpdateDto;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.exception.UnauthorizedAccessException;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final IUserService userService;

    @CacheEvict(value = "userTasks", key = "#username")
    public Task createTask(TaskCreateDto taskDto, String username) {
        logger.info("Creating task for user: {}", username);
        
        User user = userService.findByUsername(username);
        
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setUser(user);
        task.setStatus(TaskStatus.PENDING);
        
        Task savedTask = taskRepository.save(task);
        logger.info("Task created successfully: {} for user: {}", savedTask.getId(), username);
        
        return savedTask;
    }

    @CacheEvict(value = "userTasks", key = "#username")
    public Task updateTask(Long taskId, TaskUpdateDto taskDto, String username) {
        logger.info("Updating task: {} by user: {}", taskId, username);

        Task task = findTaskById(taskId);
        User currentUser = userService.findByUsername(username);
        
        if (!canUserAccessTask(task, currentUser)) {
            throw new UnauthorizedAccessException("User cannot update this task");
        }
        
        if (taskDto.getTitle() != null) {
            task.setTitle(taskDto.getTitle());
        }
        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }
        if (taskDto.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(taskDto.getStatus()));
        }
        
        Task updatedTask = taskRepository.save(task);
        logger.info("Task updated successfully: {}", taskId);
        
        return updatedTask;
    }

    @CacheEvict(value = "userTasks", key = "#username")
    public void deleteTask(Long taskId, String username) {
        logger.info("Deleting task: {} by user: {}", taskId, username);

        Task task = findAndVerifyTaskAccess(taskId, username);

        taskRepository.delete(task);
        logger.info("Task deleted successfully: {}", taskId);
    }

    private Task findAndVerifyTaskAccess(Long taskId, String username) {
        User currentUser = userService.findByUsername(username);
        Task task = findTaskById(taskId);

        if (!canUserAccessTask(task, currentUser)) {
            logger.warn("Unauthorized access attempt for task {} by user {}", taskId, username);
            throw new UnauthorizedAccessException("User cannot access this task");
        }
        return task;
    }
    
    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
    }
    
    public List<Task> findUserTasks(String username) {
        User user = userService.findByUsername(username);
        return taskRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }
    
    public List<Task> findTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
    
    public List<Task> findUserTasksByStatus(String username, TaskStatus status) {
        User user = userService.findByUsername(username);
        return taskRepository.findByUserAndStatus(user, status);
    }
    
    public List<Task> searchUserTasks(String username, String title) {
        User user = userService.findByUsername(username);
        return taskRepository.findByUserAndTitleContaining(user, title);
    }
    
    public long getUserTaskCount(String username, TaskStatus status) {
        User user = userService.findByUsername(username);
        return taskRepository.countByUserAndStatus(user, status);
    }
    
    public long getTotalTaskCount(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
    
    @Cacheable("userTasks")
    public List<Task> findUserTasksCached(String username) {
        logger.info("Fetching user tasks from DB (cached): {}", username);
        return findUserTasks(username);
    }

    @Async
    public CompletableFuture<List<Task>> findAllTasksAsync() {
        logger.info("Fetching all tasks asynchronously in a new thread");
        List<Task> tasks = taskRepository.findAll();
        return CompletableFuture.completedFuture(tasks);
    }
    
    private boolean canUserAccessTask(Task task, User user) {
        return user.getRole() == Role.ADMIN || task.getUser().getId().equals(user.getId());
    }
}