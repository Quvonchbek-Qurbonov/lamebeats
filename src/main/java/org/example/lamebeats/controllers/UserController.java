package org.example.lamebeats.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.lamebeats.dto.*;
import org.example.lamebeats.models.User;
import org.example.lamebeats.services.AuthService;
import org.example.lamebeats.services.UserService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not have permission to access this resource"));
        }

        Map<String, Object> response = userService.getAllActiveUsersPaginated(page, limit);

        // Convert entities to DTOs
        List<User> users = (List<User>) response.get("data");
        List<UserDto> userDtos = users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", userDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return userService.getUserById(CurrentUser.getCurrentUserId())
                .map(user -> ResponseEntity.ok(UserDto.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid user ID format"));
        }

        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(UserDto.fromEntity(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        // Check if username or email already exists
        if (userService.isUsernameTaken(request.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken"));
        }

        if (userService.isEmailTaken(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is already registered"));
        }

        User createdUser = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getPhoto()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserDto.fromEntityForRegistration(createdUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid user ID format"));
        }

        // Check if username is being updated and is already taken by someone else
        if (request.getUsername() != null && userService.isUsernameTaken(request.getUsername())) {
            userService.getUserByUsername(request.getUsername())
                    .filter(existingUser -> !existingUser.getId().equals(userId))
                    .ifPresent(existingUser -> {
                        throw new IllegalArgumentException("Username is already taken");
                    });
        }

        // Check if email is being updated and is already taken by someone else
        if (request.getEmail() != null && userService.isEmailTaken(request.getEmail())) {
            userService.getUserByEmail(request.getEmail())
                    .filter(existingUser -> !existingUser.getId().equals(userId))
                    .ifPresent(existingUser -> {
                        throw new IllegalArgumentException("Email is already registered");
                    });
        }

        // Create and populate a User object for the update
        User userToUpdate = new User();
        userToUpdate.setUsername(request.getUsername());
        userToUpdate.setEmail(request.getEmail());
        userToUpdate.setPhoto(request.getPhoto());

        return userService.updateUser(userId, userToUpdate)
                .map(updatedUser -> ResponseEntity.ok(UserDto.fromEntity(updatedUser)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        if (userService.softDeleteUser(userId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable String id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {

        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid user ID format"));
        }

        if (userService.changePassword(userId, currentPassword, newPassword)) {
            return ResponseEntity.ok().body(Map.of("message", "Password changed successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Current password is incorrect"));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String username) {
        List<User> users = userService.searchActiveUsersByUsername(username);

        List<UserDto> userDtos = users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", userDtos));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // Get user-agent header
            String userAgent = request.getHeader("User-Agent");

            // Authenticate user and generate token
            LoginResponse loginResponse = authService.login(loginRequest, userAgent);

            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
}