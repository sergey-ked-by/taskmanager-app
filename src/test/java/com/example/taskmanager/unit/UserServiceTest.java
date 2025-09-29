package com.example.taskmanager.unit;

import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.exception.UserAlreadyExistsException;
import com.example.taskmanager.exception.UserNotFoundException;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    // Verifies that a new user is registered successfully with default role and encoded password.
    @Test
    @DisplayName("Should successfully register a new user with USER role and encoded password")
    void should_registerNewUser_withDefaultRoleAndEncodedPassword() {
        var dto = new UserRegistrationDto("newuser", "password123", "password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.registerUser(dto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser.getUsername()).isEqualTo("newuser");
        assertThat(capturedUser.getPassword()).isEqualTo("encoded_password");
        assertThat(capturedUser.getRole()).isEqualTo(Role.USER);
    }

    // Ensures that an exception is thrown if a user tries to register with an existing username.
    @Test
    @DisplayName("Should throw UserAlreadyExistsException if username is already taken")
    void should_throwUserAlreadyExistsException_when_usernameIsTaken() {
        var dto = new UserRegistrationDto("existinguser", "password123", "password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Username already exists: existinguser");

        verify(userRepository, never()).save(any(User.class));
    }

    // Checks that a user can be successfully retrieved by their username.
    @Test
    @DisplayName("Should return user when found by username")
    void should_returnUser_when_foundByUsername() {
        var existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        User foundUser = userService.findByUsername("testuser");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
    }

    // Verifies that an exception is thrown when searching for a non-existent user.
    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found by username")
    void should_throwUserNotFoundException_when_userNotFoundByUsername() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("nonexistent"))
                .isInstanceOf(UserNotFoundException.class);
    }

    // Confirms that a user's role can be updated correctly.
    @Test
    @DisplayName("Should correctly update user role")
    void should_updateUserRole_when_userExists() {
        var user = new User();
        user.setId(1L);
        user.setRole(Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUserRole(1L, Role.ADMIN);

        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);
        assertThat(capturedUser.getRole()).isEqualTo(Role.ADMIN); // Also check the captured object
    }

    // Ensures that the repository's delete method is called when deleting a user.
    @Test
    @DisplayName("Should call delete method of repository when deleting user")
    void should_callDeleteOnRepository_when_deletingUser() {
        var userToDelete = new User();
        userToDelete.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToDelete));
        doNothing().when(userRepository).delete(userToDelete);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(userToDelete);
    }

    // Checks that an exception is thrown when trying to update the role of a non-existent user.
    @Test
    @DisplayName("Should throw UserNotFoundException when updating role for a non-existent user")
    void should_throwUserNotFoundException_when_updatingRoleForNonExistentUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserRole(99L, Role.ADMIN))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with id: 99");

        verify(userRepository, never()).save(any(User.class));
    }
}