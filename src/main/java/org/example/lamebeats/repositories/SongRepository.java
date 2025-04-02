package org.example.lamebeats.repositories;

import org.example.lamebeats.models.Album;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.Playlist;
import org.example.lamebeats.models.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {

    // Modified to include artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Song> findByTitleContainingIgnoreCase(@Param("title") String title);

    // Modified to include artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.album = :album")
    List<Song> findByAlbum(@Param("album") Album album);

    // Modified to include artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.album.id = :albumId")
    List<Song> findByAlbumId(@Param("albumId") UUID albumId);

    // Modified to include artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.duration BETWEEN :minDuration AND :maxDuration")
    List<Song> findByDurationBetween(@Param("minDuration") int minDuration, @Param("maxDuration") int maxDuration);

    // Find all active songs with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.deletedAt IS NULL")
    List<Song> findAllActive();

    // Find all active songs (paginated) - can't use FETCH with pagination directly
    @Query(value = "SELECT s FROM Song s WHERE s.deletedAt IS NULL",
            countQuery = "SELECT COUNT(s) FROM Song s WHERE s.deletedAt IS NULL")
    Page<Song> findAllActive(Pageable pageable);

    // Find active by id with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Song> findActiveById(@Param("id") UUID id);

    // Find active songs by title with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%')) AND s.deletedAt IS NULL")
    List<Song> findActiveByTitleContaining(@Param("title") String title);

    // Find songs by artist with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists a WHERE :artist MEMBER OF s.artists")
    List<Song> findByArtist(@Param("artist") Artist artist);

    // Find active songs by artist with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists a WHERE :artist MEMBER OF s.artists AND s.deletedAt IS NULL")
    List<Song> findActiveByArtist(@Param("artist") Artist artist);

    // Find songs by artist id with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists a WHERE a.id = :artistId")
    List<Song> findByArtistId(@Param("artistId") UUID artistId);

    // Find active songs by artist id with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists a WHERE a.id = :artistId AND s.deletedAt IS NULL")
    List<Song> findActiveByArtistId(@Param("artistId") UUID artistId);

    // Find active songs by album and not deleted with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.album = :album AND s.deletedAt IS NULL")
    List<Song> findActiveByAlbum(@Param("album") Album album);

    // Find active songs by album id with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.album.id = :albumId AND s.deletedAt IS NULL")
    List<Song> findActiveByAlbumId(@Param("albumId") UUID albumId);

    // Find recently added songs with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.deletedAt IS NULL ORDER BY s.createdAt DESC")
    List<Song> findRecentlyAddedSongs(Pageable pageable);

    // Find all deleted songs with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.deletedAt IS NOT NULL")
    List<Song> findAllDeleted();

    // Search songs by title or artist name with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists a WHERE " +
            "(LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND s.deletedAt IS NULL")
    List<Song> searchByTitleOrArtist(@Param("searchTerm") String searchTerm);

    // Find songs not in a specific playlist with artists
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.deletedAt IS NULL AND s.id NOT IN " +
            "(SELECT ps.songId FROM PlaylistSong ps WHERE ps.playlistId = :playlistId AND ps.deletedAt IS NULL)")
    List<Song> findSongsNotInPlaylist(@Param("playlistId") UUID playlistId);

    /**
     * Find songs by genre ID - joined through artists that have the specified genre
     */
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists a JOIN a.genres g WHERE g.id = :genreId AND s.deletedAt IS NULL")
    List<Song> findSongsByGenreId(@Param("genreId") UUID genreId);

    /**
     * Find songs by multiple genre IDs - joined through artists that have any of the specified genres
     */
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists a JOIN a.genres g WHERE g.id IN :genreIds AND s.deletedAt IS NULL")
    List<Song> findSongsByGenreIds(@Param("genreIds") Set<UUID> genreIds);

    /**
     * Find songs by genre ID with pagination - cannot use FETCH with pagination directly
     * Use a separate query to load artists after getting paged results
     */
    @Query(value = "SELECT DISTINCT s FROM Song s JOIN s.artists a JOIN a.genres g WHERE g.id = :genreId AND s.deletedAt IS NULL",
            countQuery = "SELECT COUNT(DISTINCT s) FROM Song s JOIN s.artists a JOIN a.genres g WHERE g.id = :genreId AND s.deletedAt IS NULL")
    Page<Song> findSongsByGenreId(@Param("genreId") UUID genreId, Pageable pageable);

    /**
     * Find songs by multiple genre IDs with pagination - cannot use FETCH with pagination directly
     * Use a separate query to load artists after getting paged results
     */
    @Query(value = "SELECT DISTINCT s FROM Song s JOIN s.artists a JOIN a.genres g WHERE g.id IN :genreIds AND s.deletedAt IS NULL",
            countQuery = "SELECT COUNT(DISTINCT s) FROM Song s JOIN s.artists a JOIN a.genres g WHERE g.id IN :genreIds AND s.deletedAt IS NULL")
    Page<Song> findSongsByGenreIds(@Param("genreIds") Set<UUID> genreIds, Pageable pageable);

    /**
     * Find songs by their IDs with artists eagerly loaded
     * Used to load artists after pagination
     */
    @Query("SELECT DISTINCT s FROM Song s LEFT JOIN FETCH s.artists WHERE s.id IN :ids")
    List<Song> findByIdInWithArtists(@Param("ids") List<UUID> ids);
}