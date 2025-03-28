package org.example.lamebeats.repositories;

import org.example.lamebeats.enums.UserType;
import org.example.lamebeats.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByEmailIgnoreCase(String email);

    // Find active users (not deleted)
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActive();

    // Find by username and not deleted
    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username) AND u.deletedAt IS NULL")
    Optional<User> findActiveByUsername(@Param("username") String username);

    // Find by email and not deleted
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.deletedAt IS NULL")
    Optional<User> findActiveByEmail(@Param("email") String email);

    // Check if username exists
    boolean existsByUsername(String username);

    // Check if email exists
    boolean existsByEmail(String email);

    // Find users by type
    List<User> findByType(UserType type);

    // Find active users by type
    @Query("SELECT u FROM User u WHERE u.type = :type AND u.deletedAt IS NULL")
    List<User> findActiveByType(@Param("type") UserType type);

    // Find soft-deleted users
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NOT NULL")
    List<User> findAllDeleted();

    // Find users created after a specific date
    List<User> findByCreatedAtAfter(LocalDateTime date);

    // Find by username containing (case insensitive)
    List<User> findByUsernameContainingIgnoreCase(String username);

    // Find active users by username containing (case insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')) AND u.deletedAt IS NULL")
    List<User> findActiveByUsernameContaining(@Param("username") String username);

    // Count active users by type
    @Query("SELECT COUNT(u) FROM User u WHERE u.type = :type AND u.deletedAt IS NULL")
    long countActiveUsersByType(@Param("type") UserType type);
}