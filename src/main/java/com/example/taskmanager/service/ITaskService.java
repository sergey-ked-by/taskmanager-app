package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskCreateDto;
import com.example.taskmanager.dto.TaskUpdateDto;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface defining the contract for the task management service.
 */
public interface ITaskService {

    /**
     * Creates a new task for the specified user.
     * @param taskDto DTO with data for creating the task.
     * @param username The username of the owner.
     * @return The saved task object.
     */
    Task createTask(TaskCreateDto taskDto, String username);

    /**
     * Updates an existing task.
     * @param taskId The ID of the task to update.
     * @param taskDto DTO with new data.
     * @param username The username of the user performing the operation.
     * @return The updated task object.
     */
    Task updateTask(Long taskId, TaskUpdateDto taskDto, String username);

    /**
     * Deletes a task.
     * @param taskId The ID of the task to delete.
     * @param username The username of the user performing the operation.
     */
    void deleteTask(Long taskId, String username);

    /**
     * Finds a task by its ID.
     * @param taskId The unique identifier of the task.
     * @return The found task.
     * @throws com.example.taskmanager.exception.TaskNotFoundException if the task is not found.
     */
    Task findTaskById(Long taskId);

    /**
     * Returns a list of all tasks for the specified user, sorted by creation date.
     * @param username The username.
     * @return A list of tasks.
     */
    List<Task> findUserTasks(String username);

    /**
     * Returns a list of absolutely all tasks in the system (for an administrator).
     * @return A list of all tasks.
     */
    List<Task> findAllTasks();

    /**
     * Finds tasks by a specific status.
     * @param status The status to search for.
     * @return A list of tasks with the specified status.
     */
    List<Task> findTasksByStatus(TaskStatus status);

    /**
     * Finds tasks of a specified user with a specific status.
     * @param username The username.
     * @param status The status to search for.
     * @return A list of tasks.
     */
    List<Task> findUserTasksByStatus(String username, TaskStatus status);

    /**
     * Searches for tasks of a specified user by a partial match in the title.
     * @param username The username.
     * @param title The part of the title to search for.
     * @return A list of found tasks.
     */
    List<Task> searchUserTasks(String username, String title);

    /**
     * Returns the number of tasks for a user with a specific status.
     * @param username The username.
     * @param status The status for counting.
     * @return The number of tasks.
     */
    long getUserTaskCount(String username, TaskStatus status);

    /**
     * Returns the total number of tasks in the system with a specific status.
     * @param status The status for counting.
     * @return The number of tasks.
     */
    long getTotalTaskCount(TaskStatus status);

    /**
     * Returns a cached list of user tasks.
     * @param username The username.
     * @return A cached list of tasks.
     */
    List<Task> findUserTasksCached(String username);

    /**
     * Asynchronously returns a list of all tasks in the system.
     * @return A CompletableFuture with a list of all tasks.
     */
    CompletableFuture<List<Task>> findAllTasksAsync();
}