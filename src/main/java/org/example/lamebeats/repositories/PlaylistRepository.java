package org.example.lamebeats.repositories;

import org.example.lamebeats.models.Playlist;
import org.example.lamebeats.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {

    // Find all playlists by user
    List<Playlist> findByUser(User user);

    // Find all active playlists by user
    @Query("SELECT p FROM Playlist p WHERE p.user = :user AND p.deletedAt IS NULL")
    List<Playlist> findActiveByUser(@Param("user") User user);

    // Find all active playlists by user ID
    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId AND p.deletedAt IS NULL")
    List<Playlist> findActiveByUserId(@Param("userId") UUID userId);

    // Find all active playlists by user ID (paginated)
    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId AND p.deletedAt IS NULL")
    Page<Playlist> findActiveByUserIdPaginated(@Param("userId") UUID userId, Pageable pageable);

    // Find all active playlists
    @Query("SELECT p FROM Playlist p WHERE p.deletedAt IS NULL")
    List<Playlist> findAllActive();

    // Find all active playlists (paginated)
    @Query("SELECT p FROM Playlist p WHERE p.deletedAt IS NULL")
    Page<Playlist> findAllActive(Pageable pageable);

    // Find active playlist by ID
    @Query("SELECT p FROM Playlist p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Playlist> findActiveById(@Param("id") UUID id);

    // Find by name containing (case insensitive)
    List<Playlist> findByNameContainingIgnoreCase(String name);

    // Find active playlists by name containing (case insensitive)
    @Query("SELECT p FROM Playlist p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.deletedAt IS NULL")
    List<Playlist> findActiveByNameContaining(@Param("name") String name);

    // Find all deleted playlists
    @Query("SELECT p FROM Playlist p WHERE p.deletedAt IS NOT NULL")
    List<Playlist> findAllDeleted();

    // Find deleted playlists by user
    @Query("SELECT p FROM Playlist p WHERE p.user = :user AND p.deletedAt IS NOT NULL")
    List<Playlist> findDeletedByUser(@Param("user") User user);

    // Count active playlists by user
    @Query("SELECT COUNT(p) FROM Playlist p WHERE p.user = :user AND p.deletedAt IS NULL")
    long countActivePlaylistsByUser(@Param("user") User user);

    // Find playlists containing song
    @Query("SELECT p FROM Playlist p JOIN p.songs s WHERE s.id = :songId AND p.deletedAt IS NULL")
    List<Playlist> findPlaylistsContainingSong(@Param("songId") UUID songId);

    // Find playlists by user and containing song
    @Query("SELECT p FROM Playlist p JOIN p.songs s WHERE p.user.id = :userId AND s.id = :songId AND p.deletedAt IS NULL")
    List<Playlist> findPlaylistsByUserContainingSong(@Param("userId") UUID userId, @Param("songId") UUID songId);
}