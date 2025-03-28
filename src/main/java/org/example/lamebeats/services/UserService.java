package org.example.lamebeats.services;

import org.example.lamebeats.enums.UserType;
import org.example.lamebeats.models.User;
import org.example.lamebeats.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Find all users regardless of deleted status
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Return list of paginated users
     */
    public Map<String, Object> getAllActiveUsersPaginated(int page, int limit) {
        // Ensure page number is not negative (Spring Data uses zero-based page index)
        int pageIndex = Math.max(page - 1, 0);

        Pageable pageable = PageRequest.of(pageIndex, limit);
        Page<User> userPage = userRepository.findAllActive(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("data", userPage.getContent());
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", userPage.getTotalPages());
        response.put("total", userPage.getTotalElements());

        return response;
    }

    /**
     * Find only active (non-deleted) users
     */
    public List<User> getAllActiveUsers() {
        return userRepository.findAllActive();
    }

    /**
     * Find user by ID
     */
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Find by username (regardless of deleted status)
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    /**
     * Find active user by username
     */
    public Optional<User> getActiveUserByUsername(String username) {
        return userRepository.findActiveByUsername(username);
    }

    /**
     * Find by email (regardless of deleted status)
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Find active user by email
     */
    public Optional<User> getActiveUserByEmail(String email) {
        return userRepository.findActiveByEmail(email);
    }

    /**
     * Check if username exists
     */
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Create a new user with password hashing
     */
    @Transactional
    public User createUser(User user) {
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Register a new user with default USER role
     */
    @Transactional
    public User registerUser(String username, String email, String password, String photo) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhoto(photo);
        user.setType(UserType.USER);

        return userRepository.save(user);
    }

    /**
     * Update user details - handles password hashing if password is changed
     */
    @Transactional
    public Optional<User> updateUser(UUID id, User userDetails) {
        return userRepository.findById(id).map(existingUser -> {
            // Update fields that can be changed
            if (userDetails.getUsername() != null) {
                existingUser.setUsername(userDetails.getUsername());
            }

            if (userDetails.getEmail() != null) {
                existingUser.setEmail(userDetails.getEmail());
            }

            // Update password if provided (and hash it)
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            if (userDetails.getPhoto() != null) {
                existingUser.setPhoto(userDetails.getPhoto());
            }

            // Only admins should be able to change user type, typically handled in controller
            if (userDetails.getType() != null) {
                existingUser.setType(userDetails.getType());
            }

            return userRepository.save(existingUser);
        });
    }

    /**
     * Change user password
     */
    @Transactional
    public boolean changePassword(UUID id, String currentPassword, String newPassword) {
        return userRepository.findById(id).map(user -> {
            // Verify current password is correct
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                // Set new password
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
            return false;
        }).orElse(false);
    }

    /**
     * Soft delete user
     */
    @Transactional
    public boolean softDeleteUser(UUID id) {
        return userRepository.findById(id).map(user -> {
            user.softDelete();
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    /**
     * Restore soft deleted user
     */
    @Transactional
    public boolean restoreUser(UUID id) {
        return userRepository.findById(id).map(user -> {
            user.restore();
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    /**
     * Hard delete user (use with caution)
     */
    @Transactional
    public boolean hardDeleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Find users by type
     */
    public List<User> getUsersByType(UserType type) {
        return userRepository.findByType(type);
    }

    /**
     * Find active users by type
     */
    public List<User> getActiveUsersByType(UserType type) {
        return userRepository.findActiveByType(type);
    }

    /**
     * Find all deleted users
     */
    public List<User> getAllDeletedUsers() {
        return userRepository.findAllDeleted();
    }

    /**
     * Search users by username
     */
    public List<User> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    /**
     * Search active users by username
     */
    public List<User> searchActiveUsersByUsername(String username) {
        return userRepository.findActiveByUsernameContaining(username);
    }

    /**
     * Count active users by type
     */
    public long countActiveUsersByType(UserType type) {
        return userRepository.countActiveUsersByType(type);
    }

    /**
     * Verify if password matches for user authentication
     */
    public boolean verifyPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}