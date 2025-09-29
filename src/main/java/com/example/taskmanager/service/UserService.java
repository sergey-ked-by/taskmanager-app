package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.exception.UserAlreadyExistsException;
import com.example.taskmanager.exception.UserNotFoundException;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements IUserService  {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        logger.debug("User found: {} with role: {}", username, user.getRole());
        return user;
    }
    
    public User registerUser(UserRegistrationDto registrationDto) {
        logger.info("Registering new user: {}", registrationDto.getUsername());
        
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + registrationDto.getUsername());
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + registrationDto.getEmail());
        }
        
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(Role.USER);
        user.setEmail(registrationDto.getEmail());
        
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> findActiveUsers() {
        return userRepository.findAllActiveUsers();
    }
    
    public User updateUserRole(Long userId, Role role) {
        User user = findById(userId);
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        logger.info("User role updated: {} -> {}", user.getUsername(), role);
        return updatedUser;
    }
    
    public void deleteUser(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
        logger.info("User deleted: {}", user.getUsername());
    }
    
    public long getUserCount() {
        return userRepository.count();
    }
    
    public long getAdminCount() {
        return userRepository.countByRole(Role.ADMIN);
    }
    
    public long getRegularUserCount() {
        return userRepository.countByRole(Role.USER);
    }
    
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}