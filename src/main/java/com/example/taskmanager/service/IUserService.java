package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Interface defining the contract for the user management service.
 */
public interface IUserService extends UserDetailsService {

    /**
     * Registers a new user in the system.
     * @param registrationDto DTO with registration data.
     * @return The saved user object.
     */
    User registerUser(UserRegistrationDto registrationDto);

    /**
     * Finds a user by their username.
     * @param username The username.
     * @return The found user.
     * @throws com.example.taskmanager.exception.UserNotFoundException if the user is not found.
     */
    User findByUsername(String username);

    /**
     * Finds a user by their ID.
     * @param id The unique identifier of the user.
     * @return The found user.
     * @throws com.example.taskmanager.exception.UserNotFoundException if the user is not found.
     */
    User findById(Long id);

    /**
     * Returns a list of all users in the system.
     * @return A list of all users.
     */
    List<User> findAllUsers();

    /**
     * Returns a list of all active users.
     * @return A list of active users.
     */
    List<User> findActiveUsers();

    /**
     * Updates a user's role.
     * @param userId The ID of the user to update.
     * @param role The new role.
     * @return The user with the updated role.
     */
    User updateUserRole(Long userId, Role role);

    /**
     * Deletes a user by their ID.
     * @param userId The ID of the user to delete.
     */
    void deleteUser(Long userId);

    /**
     * Returns the total number of users.
     * @return The number of users.
     */
    long getUserCount();

    /**
     * Returns the number of administrators.
     * @return The number of administrators.
     */
    long getAdminCount();

    /**
     * Returns the number of regular users (not administrators).
     * @return The number of regular users.
     */
    long getRegularUserCount();

    /**
     * Checks if the specified username is available for registration.
     * @param username The username to check.
     * @return {@code true} if the username is available, otherwise {@code false}.
     */
    boolean isUsernameAvailable(String username);

    /**
     * Checks if the specified email is available for registration.
     * @param email The email to check.
     * @return {@code true} if the email is available, otherwise {@code false}.
     */
    boolean isEmailAvailable(String email);
}