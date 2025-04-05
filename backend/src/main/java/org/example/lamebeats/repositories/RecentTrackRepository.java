package org.example.lamebeats.repositories;

import org.example.lamebeats.models.RecentTrack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecentTrackRepository extends JpaRepository<RecentTrack, UUID> {

    // Find recent tracks by user
    List<RecentTrack> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Find recent tracks by user with pagination
    List<RecentTrack> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // Find track by user and song
    Optional<RecentTrack> findByUserIdAndSongId(UUID userId, UUID songId);

    // Find tracks by a specific time range
    List<RecentTrack> findByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    // Find most played songs (globally)
    @Query("SELECT rt.song, COUNT(rt) as playCount FROM RecentTrack rt " +
            "GROUP BY rt.song ORDER BY playCount DESC")
    List<Object[]> findMostPlayedSongs(Pageable pageable);

    // Find most played songs by user
    @Query("SELECT rt.song, COUNT(rt) as playCount FROM RecentTrack rt " +
            "WHERE rt.user.id = :userId GROUP BY rt.song ORDER BY playCount DESC")
    List<Object[]> findMostPlayedSongsByUser(@Param("userId") UUID userId, Pageable pageable);

    // Delete old recent tracks history
    void deleteByCreatedAtBefore(LocalDateTime date);

    // Count plays for a specific song
    @Query("SELECT COUNT(rt) FROM RecentTrack rt WHERE rt.song.id = :songId")
    long countPlaysBySongId(@Param("songId") UUID songId);

    // Count plays for a specific song by user
    @Query("SELECT COUNT(rt) FROM RecentTrack rt WHERE rt.song.id = :songId AND rt.user.id = :userId")
    long countPlaysBySongIdAndUserId(@Param("songId") UUID songId, @Param("userId") UUID userId);
}