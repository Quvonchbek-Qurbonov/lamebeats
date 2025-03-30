package org.example.lamebeats.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.lamebeats.dto.LoginRequest;
import org.example.lamebeats.dto.LoginResponse;
import org.example.lamebeats.models.User;
import org.example.lamebeats.services.AuthService;
import org.example.lamebeats.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

        Map<String, Object> response = userService.getAllActiveUsersPaginated(page, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Check if username or email already exists
        if (userService.isUsernameTaken(user.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is already taken"));
        }


        if (userService.isEmailTaken(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is already registered"));
        }

        User createdUser = userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword(), user.getPhoto());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody User user) {
        // Check if username is being updated and is already taken by someone else
        if (user.getUsername() != null && userService.isUsernameTaken(user.getUsername())) {
            userService.getUserByUsername(user.getUsername())
                    .filter(existingUser -> !existingUser.getId().equals(id))
                    .ifPresent(existingUser -> {
                        throw new IllegalArgumentException("Username is already taken");
                    });
        }

        // Check if email is being updated and is already taken by someone else
        if (user.getEmail() != null && userService.isEmailTaken(user.getEmail())) {
            userService.getUserByEmail(user.getEmail())
                    .filter(existingUser -> !existingUser.getId().equals(id))
                    .ifPresent(existingUser -> {
                        throw new IllegalArgumentException("Email is already registered");
                    });
        }

        return userService.updateUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        if (userService.softDeleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable UUID id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {

        if (userService.changePassword(id, currentPassword, newPassword)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Current password is incorrect"));
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String username) {
        return userService.searchActiveUsersByUsername(username);
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
}