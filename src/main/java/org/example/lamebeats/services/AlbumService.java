package org.example.lamebeats.services;

import org.example.lamebeats.models.Album;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.User;
import org.example.lamebeats.repositories.AlbumRepository;
import org.example.lamebeats.repositories.ArtistRepository;
import org.example.lamebeats.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository,
                        UserRepository userRepository,
                        ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
    }

    /**
     * Get all active albums with pagination
     */
    public Map<String, Object> getAllActiveAlbumsPaginated(int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("releaseDate").descending());

        Page<Album> albumPage = albumRepository.findAllActivePaginated(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("data", albumPage.getContent());
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", albumPage.getTotalPages());
        response.put("total", albumPage.getTotalElements());

        return response;
    }

    /**
     * Get album by ID
     */
    public Optional<Album> getAlbumById(UUID id) {
        return albumRepository.findById(id);
    }

    /**
     * Get active album by ID
     */
    public Optional<Album> getActiveAlbumById(UUID id) {
        return albumRepository.findActiveById(id);
    }

    /**
     * Search albums by title
     */
    public List<Album> searchAlbumsByTitle(String title) {
        return albumRepository.findActiveByTitleContaining(title);
    }

    /**
     * Get active albums by artist ID
     */
    public List<Album> getActiveAlbumsByArtistId(UUID artistId) {
        return albumRepository.findActiveByArtistId(artistId);
    }

    /**
     * Create a new album
     */
    @Transactional
    public Album createAlbum(String title, LocalDate releaseDate, String photo, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Artist artist = artistRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));

        Album album = new Album();
        album.setTitle(title);
        album.setReleaseDate(releaseDate);
        album.setPhoto(photo);
        album.setArtists(Set.of(artist));

        return albumRepository.save(album);
    }

    /**
     * Update album details
     */
    @Transactional
    public Optional<Album> updateAlbum(UUID albumId, String title, LocalDate releaseDate, String photo) {
        return albumRepository.findActiveById(albumId).map(album -> {
            if (title != null && !title.trim().isEmpty()) {
                album.setTitle(title);
            }

            if (releaseDate != null) {
                album.setReleaseDate(releaseDate);
            }

            if (photo != null) {
                album.setPhoto(photo);
            }

            return albumRepository.save(album);
        });
    }

    /**
     * Soft delete an album
     */
    @Transactional
    public boolean softDeleteAlbum(UUID albumId) {
        return albumRepository.findActiveById(albumId).map(album -> {
            album.setDeletedAt(LocalDateTime.now());
            albumRepository.save(album);
            return true;
        }).orElse(false);
    }
}