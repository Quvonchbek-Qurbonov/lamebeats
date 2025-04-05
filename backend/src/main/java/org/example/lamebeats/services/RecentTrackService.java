package org.example.lamebeats.services;

import org.example.lamebeats.models.RecentTrack;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.models.User;
import org.example.lamebeats.repositories.RecentTrackRepository;
import org.example.lamebeats.repositories.SongRepository;
import org.example.lamebeats.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RecentTrackService {

    private final RecentTrackRepository recentTrackRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    @Autowired
    public RecentTrackService(RecentTrackRepository recentTrackRepository,
                              UserRepository userRepository,
                              SongRepository songRepository) {
        this.recentTrackRepository = recentTrackRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    /**
     * Get all recent tracks
     */
    public List<RecentTrack> getAllRecentTracks() {
        return recentTrackRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * Get all recent tracks with pagination
     */
    public Map<String, Object> getAllRecentTracksPaginated(int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<RecentTrack> recentTracks = recentTrackRepository.findAll(pageable).getContent();
        long total = recentTrackRepository.count();

        Map<String, Object> response = new HashMap<>();
        response.put("data", recentTracks);
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", (int) Math.ceil((double) total / limit));
        response.put("total", total);

        return response;
    }

    /**
     * Get recent tracks by user
     */
    public List<RecentTrack> getRecentTracksByUser(UUID userId) {
        return recentTrackRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get recent tracks by user with pagination
     */
    public Map<String, Object> getRecentTracksByUserPaginated(UUID userId, int page, int limit) {
        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, limit);

        List<RecentTrack> recentTracks = recentTrackRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        long total = recentTrackRepository.findByUserIdOrderByCreatedAtDesc(userId).size();

        Map<String, Object> response = new HashMap<>();
        response.put("data", recentTracks);
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", (int) Math.ceil((double) total / limit));
        response.put("total", total);

        return response;
    }

    /**
     * Get recent track by user and song
     */
    public Optional<RecentTrack> getRecentTrackByUserAndSong(UUID userId, UUID songId) {
        return recentTrackRepository.findByUserIdAndSongId(userId, songId);
    }

    /**
     * Get tracks by user in a specific time range
     */
    public List<RecentTrack> getTracksByUserInTimeRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return recentTrackRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    /**
     * Get most played songs globally
     */
    public List<Object[]> getMostPlayedSongs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return recentTrackRepository.findMostPlayedSongs(pageable);
    }

    /**
     * Get most played songs by a user
     */
    public List<Object[]> getMostPlayedSongsByUser(UUID userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return recentTrackRepository.findMostPlayedSongsByUser(userId, pageable);
    }

    /**
     * Count plays for a specific song
     */
    public long countPlaysBySong(UUID songId) {
        return recentTrackRepository.countPlaysBySongId(songId);
    }

    /**
     * Count plays for a specific song by user
     */
    public long countPlaysBySongAndUser(UUID songId, UUID userId) {
        return recentTrackRepository.countPlaysBySongIdAndUserId(songId, userId);
    }

    /**
     * Create/record a recent track
     */
    @Transactional
    public RecentTrack recordRecentTrack(UUID userId, UUID songId) {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Verify song exists
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found with ID: " + songId));

        // Check if a recent track entry already exists for this user and song
        RecentTrack recentTrack = recentTrackRepository.findByUserIdAndSongId(userId, songId)
                .orElse(new RecentTrack());

        // Update existing or set up new track
        recentTrack.setUser(user);
        recentTrack.setSong(song);
        // If it's a new track, createdAt will be set automatically
        // If it's an existing track, updatedAt will be updated

        return recentTrackRepository.save(recentTrack);
    }

    /**
     * Clean up old track history
     */
    @Transactional
    public long cleanupOldTracks(LocalDateTime beforeDate) {
        long count = recentTrackRepository.findAll().stream()
                .filter(track -> track.getCreatedAt().isBefore(beforeDate))
                .count();

        recentTrackRepository.deleteByCreatedAtBefore(beforeDate);
        return count;
    }

    /**
     * Delete a specific recent track
     */
    @Transactional
    public boolean deleteRecentTrack(UUID trackId) {
        if (recentTrackRepository.existsById(trackId)) {
            recentTrackRepository.deleteById(trackId);
            return true;
        }
        return false;
    }

    /**
     * Delete all recent tracks for a specific user
     */
    @Transactional
    public long deleteRecentTracksByUser(UUID userId) {
        List<RecentTrack> tracksToDelete = recentTrackRepository.findByUserIdOrderByCreatedAtDesc(userId);
        long count = tracksToDelete.size();

        recentTrackRepository.deleteAll(tracksToDelete);
        return count;
    }
}