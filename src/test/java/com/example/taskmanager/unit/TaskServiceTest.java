package com.example.taskmanager.unit;

import com.example.taskmanager.dto.TaskCreateDto;
import com.example.taskmanager.dto.TaskUpdateDto;
import com.example.taskmanager.exception.TaskNotFoundException; 
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.User;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.IUserService;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

// Using AssertJ for more readable and flexible assertions
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for TaskService")
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private IUserService userService;

    @InjectMocks
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    // Verifies that a task is successfully created with valid data.
    @Test
    @DisplayName("Should save and return a task with correct data")
    void should_saveAndReturnTask_when_validDtoIsProvided() {
        var dto = new TaskCreateDto();
        dto.setTitle("Новая задача");
        dto.setDescription("Описание для задачи");

        var user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(new Task());

        taskService.createTask(dto, "testuser");

        verify(taskRepository).save(taskArgumentCaptor.capture());
        Task capturedTask = taskArgumentCaptor.getValue();

        assertThat(capturedTask.getTitle()).isEqualTo("Новая задача");
        assertThat(capturedTask.getDescription()).isEqualTo("Описание для задачи");
        assertThat(capturedTask.getStatus()).isEqualTo(TaskStatus.PENDING); // Check default status
        assertThat(capturedTask.getUser()).isEqualTo(user); // Check that the correct user is assigned
    }

    // Ensures that a task can be retrieved by its ID if it exists.
    @Test
    @DisplayName("Should return a task by ID if it exists")
    void should_returnTask_when_taskExists() {
        var existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Существующая задача");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        Task foundTask = taskService.findTaskById(1L);

        assertThat(foundTask).isNotNull();
        assertThat(foundTask.getId()).isEqualTo(1L);
        assertThat(foundTask.getTitle()).isEqualTo("Существующая задача");
    }
    
    // Checks that an exception is thrown when trying to find a non-existent task.
    @Test
    @DisplayName("Should throw an exception if the task by ID is not found")
    void should_throwTaskNotFoundException_when_taskDoesNotExist() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findTaskById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found with id: 99");
    }

    // Verifies that a task's details are correctly updated.
    @Test
    @DisplayName("Should correctly update task fields")
    void should_updateAndSaveChanges_when_validUpdateDtoProvided() {
        var updateDto = new TaskUpdateDto();
        updateDto.setTitle("Обновленный заголовок");
        updateDto.setDescription("Обновленное описание");
        updateDto.setStatus("COMPLETED");

        var existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Старый заголовок");
        existingTask.setUser(new User());

        var adminUser = new User();
        adminUser.setId(99L);
        adminUser.setRole(Role.ADMIN);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(userService.findByUsername("admin")).thenReturn(adminUser);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updatedTask = taskService.updateTask(1L, updateDto, "admin");

        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getTitle()).isEqualTo("Обновленный заголовок");
        assertThat(updatedTask.getDescription()).isEqualTo("Обновленное описание");
        assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        verify(taskRepository).save(existingTask); // Make sure we saved the modified object
    }

    // Confirms that the delete method is called when a user has the required permissions.
    @Test
    @DisplayName("Should call the delete method of the repository if the user has rights")
    void should_callDeleteOnRepository_when_userIsAllowed() {
        var user = new User();
        user.setId(1L);

        var task = new Task();
        task.setId(42L);
        task.setUser(user);

        when(taskRepository.findById(42L)).thenReturn(Optional.of(task));
        when(userService.findByUsername("testuser")).thenReturn(user);
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(42L, "testuser");

        verify(taskRepository, times(1)).delete(task);
    }
    
    // Ensures that a list of tasks is correctly returned for a given user.
    @Test
    @DisplayName("Should return a list of user tasks")
    void should_returnUserTasks_when_userExists() {
        var user = new User();
        user.setId(1L);
        var task1 = new Task();
        var task2 = new Task();
        List<Task> userTasks = List.of(task1, task2);

        when(userService.findByUsername("user1")).thenReturn(user);
        when(taskRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(userTasks);

        List<Task> result = taskService.findUserTasks("user1");

        assertThat(result).isNotNull().hasSize(2).isEqualTo(userTasks);
    }

    // Verifies that a user cannot delete a task belonging to another user.
    @Test
    @DisplayName("Should throw UnauthorizedAccessException when user tries to delete another user's task")
    void should_throwUnauthorizedAccessException_when_deletingAnotherUsersTask() {
        var ownerUser = new User();
        ownerUser.setId(1L);

        var attackerUser = new User();
        attackerUser.setId(2L);

        var task = new Task();
        task.setId(42L);
        task.setUser(ownerUser);

        when(taskRepository.findById(42L)).thenReturn(Optional.of(task));
        when(userService.findByUsername("attacker")).thenReturn(attackerUser);

        assertThatThrownBy(() -> taskService.deleteTask(42L, "attacker"))
                .isInstanceOf(com.example.taskmanager.exception.UnauthorizedAccessException.class)
                                .hasMessageContaining("User cannot access this task");

        verify(taskRepository, never()).delete(any(Task.class));
    }
}