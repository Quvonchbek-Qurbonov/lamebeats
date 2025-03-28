package org.example.lamebeats.repositories;

import org.example.lamebeats.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {

    // Find by title exactly (case insensitive)
    Optional<Genre> findByTitleIgnoreCase(String title);

    // Find by title containing (case insensitive)
    List<Genre> findByTitleContainingIgnoreCase(String title);

    // Find all active genres
    @Query("SELECT g FROM Genre g WHERE g.deletedAt IS NULL")
    List<Genre> findAllActive();

    // Find active by id
    @Query("SELECT g FROM Genre g WHERE g.id = :id AND g.deletedAt IS NULL")
    Optional<Genre> findActiveById(@Param("id") UUID id);

    // Find active by title (case insensitive)
    @Query("SELECT g FROM Genre g WHERE LOWER(g.title) = LOWER(:title) AND g.deletedAt IS NULL")
    Optional<Genre> findActiveByTitle(@Param("title") String title);

    // Find active genre by title containing
    @Query("SELECT g FROM Genre g WHERE LOWER(g.title) LIKE LOWER(CONCAT('%', :title, '%')) AND g.deletedAt IS NULL")
    List<Genre> findActiveByTitleContaining(@Param("title") String title);

    // Find genres by artist id
    @Query("SELECT g FROM Genre g JOIN g.artists a WHERE a.id = :artistId")
    List<Genre> findByArtistId(@Param("artistId") UUID artistId);

    // Find active genres by artist id
    @Query("SELECT g FROM Genre g JOIN g.artists a WHERE a.id = :artistId AND g.deletedAt IS NULL")
    List<Genre> findActiveByArtistId(@Param("artistId") UUID artistId);

    // Find all deleted genres
    @Query("SELECT g FROM Genre g WHERE g.deletedAt IS NOT NULL")
    List<Genre> findAllDeleted();

    // Find most popular genres (based on number of artists)
    @Query("SELECT g, COUNT(a) as artistCount FROM Genre g JOIN g.artists a " +
            "WHERE g.deletedAt IS NULL GROUP BY g ORDER BY artistCount DESC")
    List<Genre> findMostPopularGenres(org.springframework.data.domain.Pageable pageable);
}