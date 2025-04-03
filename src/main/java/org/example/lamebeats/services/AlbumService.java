package org.example.lamebeats.services;

import org.example.lamebeats.models.Album;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.repositories.AlbumRepository;
import org.example.lamebeats.repositories.ArtistRepository;
import org.example.lamebeats.repositories.AlbumArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final AlbumArtistRepository albumArtistRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, 
                        ArtistRepository artistRepository,
                        AlbumArtistRepository albumArtistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.albumArtistRepository = albumArtistRepository;
    }

    /**
     * Get all albums (regardless of deleted status)
     */
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    /**
     * Get all active albums
     */
    public List<Album> getAllActiveAlbums() {
        return albumRepository.findAllActive();
    }
    
    /**
     * Get all active albums with pagination
     */
    public Map<String, Object> getAllActiveAlbumsPaginated(int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("releaseDate").descending());
        
        List<Album> albums = albumRepository.findAllActive();
        
        // Manual pagination since repository doesn't have a paginated method
        int total = albums.size();
        int fromIndex = pageIndex * limit;
        int toIndex = Math.min(fromIndex + limit, total);
        
        List<Album> pagedAlbums = fromIndex < total ? 
                albums.subList(fromIndex, toIndex) : new ArrayList<>();
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", pagedAlbums);
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", (int) Math.ceil((double) total / limit));
        response.put("total", total);
        
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
        return albumRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Search active albums by title
     */
    public List<Album> searchActiveAlbumsByTitle(String title) {
        return albumRepository.findActiveByTitleContaining(title);
    }
    
    /**
     * Get albums by release date range
     */
    public List<Album> getAlbumsByReleaseDateRange(LocalDate startDate, LocalDate endDate) {
        return albumRepository.findByReleaseDateBetween(startDate, endDate);
    }
    
    /**
     * Get active albums by release date range
     */
    public List<Album> getActiveAlbumsByReleaseDateRange(LocalDate startDate, LocalDate endDate) {
        return albumRepository.findActiveByReleaseDateBetween(startDate, endDate);
    }
    
    /**
     * Get albums by artist
     */
    public List<Album> getAlbumsByArtist(Artist artist) {
        return albumRepository.findByArtist(artist);
    }
    
    /**
     * Get active albums by artist
     */
    public List<Album> getActiveAlbumsByArtist(Artist artist) {
        return albumRepository.findActiveByArtist(artist);
    }
    
    /**
     * Get albums by artist ID
     */
    public List<Album> getAlbumsByArtistId(UUID artistId) {
        return albumRepository.findByArtistId(artistId);
    }
    
    /**
     * Get active albums by artist ID
     */
    public List<Album> getActiveAlbumsByArtistId(UUID artistId) {
        return albumRepository.findActiveByArtistId(artistId);
    }
    
    /**
     * Get recent albums
     */
    public List<Album> getRecentAlbums(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return albumRepository.findRecentAlbums(pageable);
    }
    
    /**
     * Get all deleted albums
     */
    public List<Album> getAllDeletedAlbums() {
        return albumRepository.findAllDeleted();
    }

    /**
     * Count songs in album
     */
    public long countSongsInAlbum(UUID albumId) {
        return albumRepository.countSongsInAlbum(albumId);
    }
    
    /**
     * Count artists for album
     */
    public long countArtistsForAlbum(UUID albumId) {
        return albumArtistRepository.countArtistsByAlbumId(albumId);
    }
    
    /**
     * Count albums for artist
     */
    public long countAlbumsForArtist(UUID artistId) {
        return albumArtistRepository.countAlbumsByArtistId(artistId);
    }

    /**
     * Create a new album
     */
    @Transactional
    public Album createAlbum(String title, LocalDate releaseDate, String photo, List<UUID> artistIds) {
        Album album = new Album();
        album.setTitle(title);
        album.setReleaseDate(releaseDate);
        album.setPhoto(photo);
        album.setArtists(new HashSet<>());
        album.setSongs(new ArrayList<>());
        
        Album savedAlbum = albumRepository.save(album);
        
        // Add artists if provided
        if (artistIds != null && !artistIds.isEmpty()) {
            addArtistsToAlbum(savedAlbum.getId(), artistIds);
            // Reload album to get the updated artist relationships
            return albumRepository.findById(savedAlbum.getId()).orElse(savedAlbum);
        }
        
        return savedAlbum;
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
     * Add artists to an album
     */
    @Transactional
    public Optional<Album> addArtistsToAlbum(UUID albumId, List<UUID> artistIds) {
        Optional<Album> albumOpt = albumRepository.findActiveById(albumId);
        
        if (albumOpt.isPresent()) {
            Album album = albumOpt.get();
            List<Artist> artists = artistRepository.findAllById(artistIds);
            
            for (Artist artist : artists) {
                album.getArtists().add(artist);
                artist.getAlbums().add(album);
            }
            
            return Optional.of(albumRepository.save(album));
        }
        
        return Optional.empty();
    }
    
    /**
     * Remove artists from an album
     */
    @Transactional
    public Optional<Album> removeArtistsFromAlbum(UUID albumId, List<UUID> artistIds) {
        Optional<Album> albumOpt = albumRepository.findActiveById(albumId);
        
        if (albumOpt.isPresent()) {
            Album album = albumOpt.get();
            
            album.getArtists().removeIf(artist -> artistIds.contains(artist.getId()));
            
            // For each artist, also update the bidirectional relationship
            List<Artist> artists = artistRepository.findAllById(artistIds);
            for (Artist artist : artists) {
                artist.getAlbums().remove(album);
                artistRepository.save(artist);
            }
            
            return Optional.of(albumRepository.save(album));
        }
        
        return Optional.empty();
    }
    
    /**
     * Set artists for an album (replacing existing ones)
     */
    @Transactional
    public Optional<Album> setArtistsForAlbum(UUID albumId, List<UUID> artistIds) {
        Optional<Album> albumOpt = albumRepository.findActiveById(albumId);
        
        if (albumOpt.isPresent()) {
            Album album = albumOpt.get();
            Set<Artist> currentArtists = new HashSet<>(album.getArtists());
            
            // Remove album from all current artists
            for (Artist artist : currentArtists) {
                artist.getAlbums().remove(album);
                artistRepository.save(artist);
            }
            
            // Clear current artists
            album.getArtists().clear();
            
            // Add new artists
            if (artistIds != null && !artistIds.isEmpty()) {
                List<Artist> newArtists = artistRepository.findAllById(artistIds);
                
                for (Artist artist : newArtists) {
                    album.getArtists().add(artist);
                    artist.getAlbums().add(album);
                    artistRepository.save(artist);
                }
            }
            
            return Optional.of(albumRepository.save(album));
        }
        
        return Optional.empty();
    }
    
    /**
     * Soft delete an album
     */
    @Transactional
    public boolean softDeleteAlbum(UUID albumId) {
        return albumRepository.findActiveById(albumId).map(album -> {
            album.softDelete();
            albumRepository.save(album);
            return true;
        }).orElse(false);
    }
    
    /**
     * Restore a soft-deleted album
     */
    @Transactional
    public boolean restoreAlbum(UUID albumId) {
        return albumRepository.findById(albumId)
                .filter(Album::isDeleted)
                .map(album -> {
                    album.restore();
                    albumRepository.save(album);
                    return true;
                }).orElse(false);
    }
    
    /**
     * Hard delete an album (use with caution)
     */
    @Transactional
    public boolean hardDeleteAlbum(UUID albumId) {
        if (albumRepository.existsById(albumId)) {
            albumRepository.deleteById(albumId);
            return true;
        }
        return false;
    }

    public Album findBySpotifyId(String spotifyId) {
        return albumRepository.findBySpotifyId(spotifyId);
    }
}