package org.example.lamebeats.services;

import org.example.lamebeats.models.Album;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.models.User;
import org.example.lamebeats.repositories.AlbumRepository;
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
public class SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    @Autowired
    public SongService(SongRepository songRepository,
                       UserRepository userRepository,
                       AlbumRepository albumRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
    }

    /**
     * Get all active songs with pagination
     */
    public Map<String, Object> getAllActiveSongsPaginated(int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("createdAt").descending());

        Page<Song> songPage = songRepository.findAllActive(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("data", songPage.getContent());
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", songPage.getTotalPages());
        response.put("total", songPage.getTotalElements());

        return response;
    }

    /**
     * Get song by ID
     */
    public Optional<Song> getSongById(UUID id) {
        return songRepository.findById(id);
    }

    /**
     * Get active song by ID
     */
    public Optional<Song> getActiveSongById(UUID id) {
        return songRepository.findActiveById(id);
    }

    /**
     * Search songs by title or artist name
     */
    public List<Song> searchSongsByTitleOrArtist(String searchTerm) {
        return songRepository.searchByTitleOrArtist(searchTerm);
    }

    /**
     * Create a new song
     */
    @Transactional
    public Song createSong(String title, String genre, int duration, UUID albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));

        Song song = new Song();
        song.setTitle(title);
        song.setGenre(genre);
        song.setDuration(duration);
        song.setAlbum(album);

        return songRepository.save(song);
    }

    /**
     * Update song details
     */
    @Transactional
    public Optional<Song> updateSong(UUID songId, String title, String genre, int duration, UUID albumId) {
        return songRepository.findActiveById(songId).map(song -> {
            if (title != null && !title.trim().isEmpty()) {
                song.setTitle(title);
            }

            if (genre != null) {
                song.setGenre(genre);
            }

            if (duration > 0) {
                song.setDuration(duration);
            }

            if (albumId != null) {
                Album album = albumRepository.findById(albumId)
                        .orElseThrow(() -> new IllegalArgumentException("Album not found"));
                song.setAlbum(album);
            }

            return songRepository.save(song);
        });
    }

    /**
     * Soft delete a song
     */
    @Transactional
    public boolean softDeleteSong(UUID songId) {
        return songRepository.findActiveById(songId).map(song -> {
            song.setDeletedAt(LocalDateTime.now());
            songRepository.save(song);
            return true;
        }).orElse(false);
    }

}