package org.example.lamebeats.services;

import org.example.lamebeats.models.Playlist;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.models.User;
import org.example.lamebeats.repositories.PlaylistRepository;
import org.example.lamebeats.repositories.SongRepository;
import org.example.lamebeats.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, 
                          UserRepository userRepository,
                          SongRepository songRepository) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    /**
     * Get all playlists (regardless of deleted status)
     */
    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    /**
     * Get all active playlists
     */
    public List<Playlist> getAllActivePlaylists() {
        return playlistRepository.findAllActive();
    }
    
    /**
     * Get all active playlists with pagination
     */
    public Map<String, Object> getAllActivePlaylistsPaginated(int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("createdAt").descending());
        
        Page<Playlist> playlistPage = playlistRepository.findAllActive(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", playlistPage.getContent());
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", playlistPage.getTotalPages());
        response.put("total", playlistPage.getTotalElements());
        
        return response;
    }

    /**
     * Get playlists by user
     */
    public List<Playlist> getPlaylistsByUser(User user) {
        return playlistRepository.findByUser(user);
    }
    
    /**
     * Get active playlists by user
     */
    public List<Playlist> getActivePlaylistsByUser(User user) {
        return playlistRepository.findActiveByUser(user);
    }
    
    /**
     * Get active playlists by user ID
     */
    public List<Playlist> getActivePlaylistsByUserId(UUID userId) {
        return playlistRepository.findActiveByUserId(userId);
    }
    
    /**
     * Get active playlists by user ID with pagination
     */
    public Map<String, Object> getActivePlaylistsByUserIdPaginated(UUID userId, int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("createdAt").descending());
        
        Page<Playlist> playlistPage = playlistRepository.findActiveByUserIdPaginated(userId, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", playlistPage.getContent());
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", playlistPage.getTotalPages());
        response.put("total", playlistPage.getTotalElements());
        
        return response;
    }

    /**
     * Get playlist by ID
     */
    public Optional<Playlist> getPlaylistById(UUID id) {
        return playlistRepository.findById(id);
    }
    
    /**
     * Get active playlist by ID
     */
    public Optional<Playlist> getActivePlaylistById(UUID id) {
        return playlistRepository.findActiveById(id);
    }
    
    /**
     * Search playlists by name
     */
    public List<Playlist> searchPlaylistsByName(String name) {
        return playlistRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Search active playlists by name
     */
    public List<Playlist> searchActivePlaylistsByName(String name) {
        return playlistRepository.findActiveByNameContaining(name);
    }
    
    /**
     * Get all deleted playlists
     */
    public List<Playlist> getAllDeletedPlaylists() {
        return playlistRepository.findAllDeleted();
    }
    
    /**
     * Get deleted playlists by user
     */
    public List<Playlist> getDeletedPlaylistsByUser(User user) {
        return playlistRepository.findDeletedByUser(user);
    }
    
    /**
     * Count active playlists by user
     */
    public long countActivePlaylistsByUser(User user) {
        return playlistRepository.countActivePlaylistsByUser(user);
    }
    
    /**
     * Find playlists containing a specific song
     */
    public List<Playlist> getPlaylistsContainingSong(UUID songId) {
        return playlistRepository.findPlaylistsContainingSong(songId);
    }
    
    /**
     * Find playlists by user containing a specific song
     */
    public List<Playlist> getPlaylistsByUserContainingSong(UUID userId, UUID songId) {
        return playlistRepository.findPlaylistsByUserContainingSong(userId, songId);
    }

    /**
     * Create a new playlist
     */
    @Transactional
    public Playlist createPlaylist(String name, String description, String photo, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setPhoto(photo);
        playlist.setUser(user);
        playlist.setSongs(new HashSet<>());
        
        return playlistRepository.save(playlist);
    }
    
    /**
     * Update playlist details
     */
    @Transactional
    public Optional<Playlist> updatePlaylist(UUID playlistId, String name, String photo, String description) {
        return playlistRepository.findActiveById(playlistId).map(playlist -> {
            if (name != null && !name.trim().isEmpty()) {
                playlist.setName(name);
            }
            
            if (description != null) {
                playlist.setDescription(description);
            }

            if (photo != null) {
                playlist.setPhoto(photo);
            }
            
            return playlistRepository.save(playlist);
        });
    }
    
    /**
     * Add a song to a playlist
     */
    @Transactional
    public Optional<Playlist> addSongToPlaylist(UUID playlistId, UUID songId) {
        Optional<Playlist> playlistOpt = playlistRepository.findActiveById(playlistId);
        Optional<Song> songOpt = songRepository.findById(songId);
        
        if (playlistOpt.isPresent() && songOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            Song song = songOpt.get();
            
            playlist.getSongs().add(song);
            return Optional.of(playlistRepository.save(playlist));
        }
        
        return Optional.empty();
    }
    
    /**
     * Remove a song from a playlist
     */
    @Transactional
    public Optional<Playlist> removeSongFromPlaylist(UUID playlistId, UUID songId) {
        Optional<Playlist> playlistOpt = playlistRepository.findActiveById(playlistId);
        Optional<Song> songOpt = songRepository.findById(songId);
        
        if (playlistOpt.isPresent() && songOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            Song song = songOpt.get();
            
            playlist.getSongs().remove(song);
            return Optional.of(playlistRepository.save(playlist));
        }
        
        return Optional.empty();
    }
    
    /**
     * Soft delete a playlist
     */
    @Transactional
    public boolean softDeletePlaylist(UUID playlistId) {
        return playlistRepository.findActiveById(playlistId).map(playlist -> {
            playlist.setDeletedAt(LocalDateTime.now());
            playlistRepository.save(playlist);
            return true;
        }).orElse(false);
    }
    
    /**
     * Restore a soft-deleted playlist
     */
    @Transactional
    public boolean restorePlaylist(UUID playlistId) {
        return playlistRepository.findById(playlistId)
                .filter(playlist -> playlist.getDeletedAt() != null)
                .map(playlist -> {
                    playlist.setDeletedAt(null);
                    playlistRepository.save(playlist);
                    return true;
                }).orElse(false);
    }
    
    /**
     * Hard delete a playlist (use with caution)
     */
    @Transactional
    public boolean hardDeletePlaylist(UUID playlistId) {
        if (playlistRepository.existsById(playlistId)) {
            playlistRepository.deleteById(playlistId);
            return true;
        }
        return false;
    }
    
    /**
     * Check if user owns playlist
     */
    public boolean isPlaylistOwnedByUser(UUID playlistId, UUID userId) {
        return playlistRepository.findById(playlistId)
                .map(playlist -> playlist.getUser().getId().equals(userId))
                .orElse(false);
    }
    
    /**
     * Check if playlist contains song
     */
    public boolean doesPlaylistContainSong(UUID playlistId, UUID songId) {
        return playlistRepository.findById(playlistId)
                .map(playlist -> playlist.getSongs().stream()
                        .anyMatch(song -> song.getId().equals(songId)))
                .orElse(false);
    }
    
    /**
     * Get song count in playlist
     */
    public int getSongCountInPlaylist(UUID playlistId) {
        return playlistRepository.findById(playlistId)
                .map(playlist -> playlist.getSongs().size())
                .orElse(0);
    }
    
    /**
     * Add multiple songs to playlist
     */
    @Transactional
    public Optional<Playlist> addSongsToPlaylist(UUID playlistId, List<UUID> songIds) {
        Optional<Playlist> playlistOpt = playlistRepository.findActiveById(playlistId);
        
        if (playlistOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            List<Song> songsToAdd = songRepository.findAllById(songIds);
            
            playlist.getSongs().addAll(songsToAdd);
            return Optional.of(playlistRepository.save(playlist));
        }
        
        return Optional.empty();
    }
    
    /**
     * Remove multiple songs from playlist
     */
    @Transactional
    public Optional<Playlist> removeSongsFromPlaylist(UUID playlistId, List<UUID> songIds) {
        Optional<Playlist> playlistOpt = playlistRepository.findActiveById(playlistId);
        
        if (playlistOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            playlist.getSongs().removeIf(song -> songIds.contains(song.getId()));
            
            return Optional.of(playlistRepository.save(playlist));
        }
        
        return Optional.empty();
    }
    
    /**
     * Clear all songs from playlist
     */
    @Transactional
    public Optional<Playlist> clearPlaylist(UUID playlistId) {
        Optional<Playlist> playlistOpt = playlistRepository.findActiveById(playlistId);
        
        if (playlistOpt.isPresent()) {
            Playlist playlist = playlistOpt.get();
            playlist.getSongs().clear();
            
            return Optional.of(playlistRepository.save(playlist));
        }
        
        return Optional.empty();
    }
}