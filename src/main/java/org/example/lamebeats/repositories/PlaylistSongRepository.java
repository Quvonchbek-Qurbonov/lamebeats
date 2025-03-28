package org.example.lamebeats.repositories;

import org.example.lamebeats.models.PlaylistSong;
import org.example.lamebeats.models.PlaylistSongId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {

    // Find all songs in a playlist
    List<PlaylistSong> findByPlaylistId(UUID playlistId);

    // Find all active songs in a playlist
    @Query("SELECT ps FROM PlaylistSong ps WHERE ps.playlistId = :playlistId AND ps.deletedAt IS NULL")
    List<PlaylistSong> findActiveByPlaylistId(@Param("playlistId") UUID playlistId);

    // Find all playlists containing a song
    List<PlaylistSong> findBySongId(UUID songId);

    // Find all active playlists containing a song
    @Query("SELECT ps FROM PlaylistSong ps WHERE ps.songId = :songId AND ps.deletedAt IS NULL")
    List<PlaylistSong> findActiveBySongId(@Param("songId") UUID songId);

    // Find a specific playlist-song relationship
    Optional<PlaylistSong> findByPlaylistIdAndSongId(UUID playlistId, UUID songId);

    // Find active playlist-song relationship
    @Query("SELECT ps FROM PlaylistSong ps WHERE ps.playlistId = :playlistId AND ps.songId = :songId AND ps.deletedAt IS NULL")
    Optional<PlaylistSong> findActiveByPlaylistIdAndSongId(@Param("playlistId") UUID playlistId, @Param("songId") UUID songId);

    // Check if a song is in a playlist
    boolean existsByPlaylistIdAndSongIdAndDeletedAtIsNull(UUID playlistId, UUID songId);

    // Count songs in a playlist
    @Query("SELECT COUNT(ps) FROM PlaylistSong ps WHERE ps.playlistId = :playlistId AND ps.deletedAt IS NULL")
    long countSongsInPlaylist(@Param("playlistId") UUID playlistId);

    // Count playlists containing a song
    @Query("SELECT COUNT(ps) FROM PlaylistSong ps WHERE ps.songId = :songId AND ps.deletedAt IS NULL")
    long countPlaylistsContainingSong(@Param("songId") UUID songId);

    // Delete all songs from a playlist
    void deleteByPlaylistId(UUID playlistId);

    // Find all soft-deleted playlist-song relationships
    @Query("SELECT ps FROM PlaylistSong ps WHERE ps.deletedAt IS NOT NULL")
    List<PlaylistSong> findAllDeleted();
}