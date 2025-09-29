package com.example.taskmanager.controller.api;

import com.example.taskmanager.dto.AuthRequest;
import com.example.taskmanager.dto.AuthResponse;
import com.example.taskmanager.dto.UserDto;
import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.model.User;
import com.example.taskmanager.security.JwtUtil;
import com.example.taskmanager.service.IUserService;
import com.example.taskmanager.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
@AllArgsConstructor
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final IUserService userService;
    private JwtUtil jwtUtil;
    private UserMapper userMapper;
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        logger.info("Login attempt for user: {}", authRequest.getLogin());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword())
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        
        User user = userService.findByUsername(authRequest.getLogin());
        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole().name());
        
        logger.info("User logged in successfully: {}", authRequest.getLogin());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        logger.info("Registration attempt for user: {}", registrationDto.getUsername());
        
        User user = userService.registerUser(registrationDto);
        logger.info("User registered successfully: {}", user.getUsername());
        
        return ResponseEntity.ok(userMapper.toDto(user));
    }
    
    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                return ResponseEntity.ok("Token is valid");
            }
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }
}