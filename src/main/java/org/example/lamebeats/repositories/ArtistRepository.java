package org.example.lamebeats.repositories;

import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    // Find by name exactly (case insensitive)
    Optional<Artist> findByNameIgnoreCase(String name);

    // Find by name containing (case insensitive)
    List<Artist> findByNameContainingIgnoreCase(String name);

    // Find all active artists
    @Query("SELECT a FROM Artist a WHERE a.deletedAt IS NULL")
    List<Artist> findAllActive();

    // Find active by id
    @Query("SELECT a FROM Artist a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Artist> findActiveById(@Param("id") UUID id);

    // Find active artists by name containing
    @Query("SELECT a FROM Artist a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) AND a.deletedAt IS NULL")
    List<Artist> findActiveByNameContaining(@Param("name") String name);

    // Find artists by genre
    @Query("SELECT a FROM Artist a JOIN a.genres g WHERE g = :genre")
    List<Artist> findByGenre(@Param("genre") Genre genre);

    // Find active artists by genre
    @Query("SELECT a FROM Artist a JOIN a.genres g WHERE g = :genre AND a.deletedAt IS NULL")
    List<Artist> findActiveByGenre(@Param("genre") Genre genre);

    // Find artists by genre id
    @Query("SELECT a FROM Artist a JOIN a.genres g WHERE g.id = :genreId")
    List<Artist> findByGenreId(@Param("genreId") UUID genreId);

    // Find active artists by genre id
    @Query("SELECT a FROM Artist a JOIN a.genres g WHERE g.id = :genreId AND a.deletedAt IS NULL")
    List<Artist> findActiveByGenreId(@Param("genreId") UUID genreId);

    // Find artists with the most songs
    @Query("SELECT a, COUNT(s) as songCount FROM Artist a JOIN a.songs s WHERE a.deletedAt IS NULL " +
            "GROUP BY a ORDER BY songCount DESC")
    List<Artist> findArtistsWithMostSongs(org.springframework.data.domain.Pageable pageable);

    // Find all deleted artists
    @Query("SELECT a FROM Artist a WHERE a.deletedAt IS NOT NULL")
    List<Artist> findAllDeleted();

    Artist findBySpotifyId(String spotifyId);
}