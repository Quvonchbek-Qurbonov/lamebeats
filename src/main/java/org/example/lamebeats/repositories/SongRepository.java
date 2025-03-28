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
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID> {

    // Find by title (case insensitive)
    List<Song> findByTitleContainingIgnoreCase(String title);

    // Find songs by album
    List<Song> findByAlbum(Album album);

    // Find songs by album id
    List<Song> findByAlbumId(UUID albumId);

    // Find songs by genre
    List<Song> findByGenreIgnoreCase(String genre);

    // Find songs by duration range
    List<Song> findByDurationBetween(int minDuration, int maxDuration);

    // Find all active songs
    @Query("SELECT s FROM Song s WHERE s.deletedAt IS NULL")
    List<Song> findAllActive();

    // Find all active songs (paginated)
    @Query("SELECT s FROM Song s WHERE s.deletedAt IS NULL")
    Page<Song> findAllActive(Pageable pageable);

    // Find active by id
    @Query("SELECT s FROM Song s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Song> findActiveById(@Param("id") UUID id);

    // Find active songs by title
    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%')) AND s.deletedAt IS NULL")
    List<Song> findActiveByTitleContaining(@Param("title") String title);

    // Find songs by artist
    @Query("SELECT s FROM Song s JOIN s.artists a WHERE a = :artist")
    List<Song> findByArtist(@Param("artist") Artist artist);

    // Find active songs by artist
    @Query("SELECT s FROM Song s JOIN s.artists a WHERE a = :artist AND s.deletedAt IS NULL")
    List<Song> findActiveByArtist(@Param("artist") Artist artist);

    // Find songs by artist id
    @Query("SELECT s FROM Song s JOIN s.artists a WHERE a.id = :artistId")
    List<Song> findByArtistId(@Param("artistId") UUID artistId);

    // Find active songs by artist id
    @Query("SELECT s FROM Song s JOIN s.artists a WHERE a.id = :artistId AND s.deletedAt IS NULL")
    List<Song> findActiveByArtistId(@Param("artistId") UUID artistId);

    // Find active songs by album and not deleted
    @Query("SELECT s FROM Song s WHERE s.album = :album AND s.deletedAt IS NULL")
    List<Song> findActiveByAlbum(@Param("album") Album album);

    // Find active songs by album id
    @Query("SELECT s FROM Song s WHERE s.album.id = :albumId AND s.deletedAt IS NULL")
    List<Song> findActiveByAlbumId(@Param("albumId") UUID albumId);

    // Find recently added songs
    @Query("SELECT s FROM Song s WHERE s.deletedAt IS NULL ORDER BY s.createdAt DESC")
    List<Song> findRecentlyAddedSongs(Pageable pageable);

    // Find all deleted songs
    @Query("SELECT s FROM Song s WHERE s.deletedAt IS NOT NULL")
    List<Song> findAllDeleted();

    // Search songs by title or artist name
    @Query("SELECT DISTINCT s FROM Song s JOIN s.artists a WHERE " +
            "(LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND s.deletedAt IS NULL")
    List<Song> searchByTitleOrArtist(@Param("searchTerm") String searchTerm);

    // Find songs not in a specific playlist
    @Query("SELECT s FROM Song s WHERE s.deletedAt IS NULL AND s.id NOT IN " +
            "(SELECT ps.songId FROM PlaylistSong ps WHERE ps.playlistId = :playlistId AND ps.deletedAt IS NULL)")
    List<Song> findSongsNotInPlaylist(@Param("playlistId") UUID playlistId);
}