package org.example.lamebeats.repositories;

import org.example.lamebeats.models.Album;
import org.example.lamebeats.models.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {

    // Find by title (case insensitive)
    List<Album> findByTitleContainingIgnoreCase(String title);

    // Find all active albums
    @Query("SELECT a FROM Album a WHERE a.deletedAt IS NULL")
    List<Album> findAllActive();

    // Find active by id
    @Query("SELECT a FROM Album a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Album> findActiveById(@Param("id") UUID id);

    // Find active albums by title
    @Query("SELECT a FROM Album a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%')) AND a.deletedAt IS NULL")
    List<Album> findActiveByTitleContaining(@Param("title") String title);

    // Find by release date range
    List<Album> findByReleaseDateBetween(LocalDate startDate, LocalDate endDate);

    // Find active by release date range
    @Query("SELECT a FROM Album a WHERE a.releaseDate BETWEEN :startDate AND :endDate AND a.deletedAt IS NULL")
    List<Album> findActiveByReleaseDateBetween(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    // Find albums by artist
    @Query("SELECT a FROM Album a JOIN a.artists art WHERE art = :artist")
    List<Album> findByArtist(@Param("artist") Artist artist);

    // Find active albums by artist
    @Query("SELECT a FROM Album a JOIN a.artists art WHERE art = :artist AND a.deletedAt IS NULL")
    List<Album> findActiveByArtist(@Param("artist") Artist artist);

    // Find albums by artist id
    @Query("SELECT a FROM Album a JOIN a.artists art WHERE art.id = :artistId")
    List<Album> findByArtistId(@Param("artistId") UUID artistId);

    // Find active albums by artist id
    @Query("SELECT a FROM Album a JOIN a.artists art WHERE art.id = :artistId AND a.deletedAt IS NULL")
    List<Album> findActiveByArtistId(@Param("artistId") UUID artistId);

    // Find recent albums
    @Query("SELECT a FROM Album a WHERE a.deletedAt IS NULL ORDER BY a.releaseDate DESC")
    List<Album> findRecentAlbums(Pageable pageable);

    // Find all deleted albums
    @Query("SELECT a FROM Album a WHERE a.deletedAt IS NOT NULL")
    List<Album> findAllDeleted();

    // Count songs in album
    @Query("SELECT COUNT(s) FROM Song s WHERE s.album.id = :albumId")
    long countSongsInAlbum(@Param("albumId") UUID albumId);

    Album findBySpotifyId(String spotifyId);
}