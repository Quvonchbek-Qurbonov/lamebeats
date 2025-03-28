package org.example.lamebeats.repositories;

import org.example.lamebeats.models.Lyrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LyricsRepository extends JpaRepository<Lyrics, UUID> {

    // Find lyrics by song id
    List<Lyrics> findBySongId(UUID songId);

    // Find active lyrics by song id
    @Query("SELECT l FROM Lyrics l WHERE l.song.id = :songId AND l.deletedAt IS NULL")
    List<Lyrics> findActiveBySongId(@Param("songId") UUID songId);

    // Find lyrics by song id and language
    Optional<Lyrics> findBySongIdAndLanguage(UUID songId, Lyrics.Language language);

    // Find active lyrics by song id and language
    @Query("SELECT l FROM Lyrics l WHERE l.song.id = :songId AND l.language = :language AND l.deletedAt IS NULL")
    Optional<Lyrics> findActiveBySongIdAndLanguage(@Param("songId") UUID songId, @Param("language") Lyrics.Language language);

    // Find all active lyrics
    @Query("SELECT l FROM Lyrics l WHERE l.deletedAt IS NULL")
    List<Lyrics> findAllActive();

    // Find active by id
    @Query("SELECT l FROM Lyrics l WHERE l.id = :id AND l.deletedAt IS NULL")
    Optional<Lyrics> findActiveById(@Param("id") UUID id);

    // Find all lyrics by language
    List<Lyrics> findByLanguage(Lyrics.Language language);

    // Find active lyrics by language
    @Query("SELECT l FROM Lyrics l WHERE l.language = :language AND l.deletedAt IS NULL")
    List<Lyrics> findActiveByLanguage(@Param("language") Lyrics.Language language);

    // Check if lyrics exist for a song
    boolean existsBySongId(UUID songId);

    // Check if lyrics exist for a song in a specific language
    boolean existsBySongIdAndLanguage(UUID songId, Lyrics.Language language);

    // Check if active lyrics exist for a song in a specific language
    @Query("SELECT COUNT(l) > 0 FROM Lyrics l WHERE l.song.id = :songId AND l.language = :language AND l.deletedAt IS NULL")
    boolean existsActiveBySongIdAndLanguage(@Param("songId") UUID songId, @Param("language") Lyrics.Language language);

    // Find all deleted lyrics
    @Query("SELECT l FROM Lyrics l WHERE l.deletedAt IS NOT NULL")
    List<Lyrics> findAllDeleted();
}