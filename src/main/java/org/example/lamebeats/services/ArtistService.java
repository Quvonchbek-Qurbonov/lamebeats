package org.example.lamebeats.services;

import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.Genre;
import org.example.lamebeats.models.ArtistGenre;
import org.example.lamebeats.models.ArtistGenreId;
import org.example.lamebeats.repositories.ArtistRepository;
import org.example.lamebeats.repositories.GenreRepository;
import org.example.lamebeats.repositories.ArtistGenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final ArtistGenreRepository artistGenreRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository, 
                       GenreRepository genreRepository,
                       ArtistGenreRepository artistGenreRepository) {
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
        this.artistGenreRepository = artistGenreRepository;
    }

    /**
     * Get all artists (regardless of deleted status)
     */
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    /**
     * Get all active artists
     */
    public List<Artist> getAllActiveArtists() {
        return artistRepository.findAllActive();
    }
    
    /**
     * Get all active artists with pagination
     */
    public Map<String, Object> getAllActiveArtistsPaginated(int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("name").ascending());
        
        List<Artist> artists = artistRepository.findAllActive();
        
        // Manual pagination since repository doesn't have a paginated method
        int total = artists.size();
        int fromIndex = pageIndex * limit;
        int toIndex = Math.min(fromIndex + limit, total);
        
        List<Artist> pagedArtists = fromIndex < total ? 
                artists.subList(fromIndex, toIndex) : new ArrayList<>();
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", pagedArtists);
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", (int) Math.ceil((double) total / limit));
        response.put("total", total);
        
        return response;
    }

    /**
     * Get artist by ID
     */
    public Optional<Artist> getArtistById(UUID id) {
        return artistRepository.findById(id);
    }
    
    /**
     * Get active artist by ID
     */
    public Optional<Artist> getActiveArtistById(UUID id) {
        return artistRepository.findActiveById(id);
    }
    
    /**
     * Get artist by name (exact match, case insensitive)
     */
    public Optional<Artist> getArtistByName(String name) {
        return artistRepository.findByNameIgnoreCase(name);
    }
    
    /**
     * Search artists by name (partial match)
     */
    public List<Artist> searchArtistsByName(String name) {
        return artistRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Search active artists by name (partial match)
     */
    public List<Artist> searchActiveArtistsByName(String name) {
        return artistRepository.findActiveByNameContaining(name);
    }
    
    /**
     * Get all deleted artists
     */
    public List<Artist> getAllDeletedArtists() {
        return artistRepository.findAllDeleted();
    }
    
    /**
     * Get artists by genre
     */
    public List<Artist> getArtistsByGenre(Genre genre) {
        return artistRepository.findByGenre(genre);
    }
    
    /**
     * Get active artists by genre
     */
    public List<Artist> getActiveArtistsByGenre(Genre genre) {
        return artistRepository.findActiveByGenre(genre);
    }
    
    /**
     * Get artists by genre ID
     */
    public List<Artist> getArtistsByGenreId(UUID genreId) {
        return artistRepository.findByGenreId(genreId);
    }
    
    /**
     * Get active artists by genre ID
     */
    public List<Artist> getActiveArtistsByGenreId(UUID genreId) {
        return artistRepository.findActiveByGenreId(genreId);
    }
    
    /**
     * Get artists with the most songs
     */
    public List<Artist> getArtistsWithMostSongs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return artistRepository.findArtistsWithMostSongs(pageable);
    }

    /**
     * Create a new artist
     */
    @Transactional
    public Artist createArtist(String name, String photo) {
        // Check if artist with this name already exists
        Optional<Artist> existingArtist = artistRepository.findByNameIgnoreCase(name);
        if (existingArtist.isPresent()) {
            throw new IllegalStateException("Artist with name '" + name + "' already exists");
        }
        
        Artist artist = new Artist();
        artist.setName(name);
        artist.setPhoto(photo);
        artist.setGenres(new HashSet<>());
        
        return artistRepository.save(artist);
    }
    
    /**
     * Update artist details
     */
    @Transactional
    public Optional<Artist> updateArtist(UUID artistId, String name, String photo) {
        return artistRepository.findActiveById(artistId).map(artist -> {
            // Check if another artist with this name already exists
            if (name != null && !name.trim().isEmpty()) {
                Optional<Artist> existingArtist = artistRepository.findByNameIgnoreCase(name);
                if (existingArtist.isPresent() && !existingArtist.get().getId().equals(artistId)) {
                    throw new IllegalStateException("Another artist with name '" + name + "' already exists");
                }
                artist.setName(name);
            }
            
            if (photo != null) {
                artist.setPhoto(photo);
            }
            
            return artistRepository.save(artist);
        });
    }
    
    /**
     * Soft delete an artist
     */
    @Transactional
    public boolean softDeleteArtist(UUID artistId) {
        return artistRepository.findActiveById(artistId).map(artist -> {
            artist.softDelete();
            artistRepository.save(artist);
            return true;
        }).orElse(false);
    }
    
    /**
     * Restore a soft-deleted artist
     */
    @Transactional
    public boolean restoreArtist(UUID artistId) {
        return artistRepository.findById(artistId)
                .filter(Artist::isDeleted)
                .map(artist -> {
                    artist.restore();
                    artistRepository.save(artist);
                    return true;
                }).orElse(false);
    }
    
    /**
     * Hard delete an artist (use with caution)
     */
    @Transactional
    public boolean hardDeleteArtist(UUID artistId) {
        if (artistRepository.existsById(artistId)) {
            artistRepository.deleteById(artistId);
            return true;
        }
        return false;
    }
    
    /**
     * Add a genre to an artist
     */
    @Transactional
    public Optional<Artist> addGenreToArtist(UUID artistId, UUID genreId) {
        Optional<Artist> artistOpt = artistRepository.findActiveById(artistId);
        Optional<Genre> genreOpt = genreRepository.findActiveById(genreId);
        
        if (artistOpt.isPresent() && genreOpt.isPresent()) {
            Artist artist = artistOpt.get();
            Genre genre = genreOpt.get();
            
            artist.getGenres().add(genre);
            genre.getArtists().add(artist);
            
            genreRepository.save(genre);
            return Optional.of(artistRepository.save(artist));
        }
        
        return Optional.empty();
    }
    
    /**
     * Remove a genre from an artist
     */
    @Transactional
    public Optional<Artist> removeGenreFromArtist(UUID artistId, UUID genreId) {
        Optional<Artist> artistOpt = artistRepository.findActiveById(artistId);
        Optional<Genre> genreOpt = genreRepository.findById(genreId);
        
        if (artistOpt.isPresent() && genreOpt.isPresent()) {
            Artist artist = artistOpt.get();
            Genre genre = genreOpt.get();
            
            artist.getGenres().remove(genre);
            genre.getArtists().remove(artist);
            
            genreRepository.save(genre);
            return Optional.of(artistRepository.save(artist));
        }
        
        return Optional.empty();
    }
    
    /**
     * Add multiple genres to an artist
     */
    @Transactional
    public Optional<Artist> addGenresToArtist(UUID artistId, List<UUID> genreIds) {
        Optional<Artist> artistOpt = artistRepository.findActiveById(artistId);
        
        if (artistOpt.isPresent()) {
            Artist artist = artistOpt.get();
            List<Genre> genresToAdd = genreRepository.findAllById(genreIds);
            
            for (Genre genre : genresToAdd) {
                artist.getGenres().add(genre);
                genre.getArtists().add(artist);
                genreRepository.save(genre);
            }
            
            return Optional.of(artistRepository.save(artist));
        }
        
        return Optional.empty();
    }
    
    /**
     * Set genres for an artist (replacing existing genres)
     */
    @Transactional
    public Optional<Artist> setGenresForArtist(UUID artistId, List<UUID> genreIds) {
        Optional<Artist> artistOpt = artistRepository.findActiveById(artistId);
        
        if (artistOpt.isPresent()) {
            Artist artist = artistOpt.get();
            
            // Remove artist from all current genres
            for (Genre genre : artist.getGenres()) {
                genre.getArtists().remove(artist);
                genreRepository.save(genre);
            }
            
            // Clear current genres
            artist.getGenres().clear();
            
            // Add new genres
            if (genreIds != null && !genreIds.isEmpty()) {
                List<Genre> genresToAdd = genreRepository.findAllById(genreIds);
                
                for (Genre genre : genresToAdd) {
                    artist.getGenres().add(genre);
                    genre.getArtists().add(artist);
                    genreRepository.save(genre);
                }
            }
            
            return Optional.of(artistRepository.save(artist));
        }
        
        return Optional.empty();
    }
    
    /**
     * Get the number of artists associated with a genre
     */
    public long getArtistCountForGenre(UUID genreId) {
        return artistGenreRepository.countArtistsByGenreId(genreId);
    }
    
    /**
     * Get the number of genres associated with an artist
     */
    public long getGenreCountForArtist(UUID artistId) {
        return artistGenreRepository.countGenresByArtistId(artistId);
    }
    
    /**
     * Get the most popular genres (based on number of artists)
     */
    public List<Object[]> getPopularGenres(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return artistGenreRepository.countArtistsByGenre(pageable);
    }
    
    /**
     * Check if artist exists by name (case insensitive)
     */
    public boolean existsByName(String name) {
        return artistRepository.findByNameIgnoreCase(name).isPresent();
    }
    
    /**
     * Get or create artist
     */
    @Transactional
    public Artist getOrCreateArtist(String name, String photo) {
        return artistRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> createArtist(name, photo));
    }
}