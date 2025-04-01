package org.example.lamebeats.services;

import org.example.lamebeats.models.Genre;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.repositories.GenreRepository;
import org.example.lamebeats.repositories.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository, ArtistRepository artistRepository) {
        this.genreRepository = genreRepository;
        this.artistRepository = artistRepository;
    }

    /**
     * Get all genres (regardless of deleted status)
     */
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    /**
     * Get all active genres
     */
    public List<Genre> getAllActiveGenres() {
        return genreRepository.findAllActive();
    }
    
    /**
     * Get all active genres with pagination
     */
    public Map<String, Object> getAllActiveGenresPaginated(int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("title").ascending());
        
        List<Genre> genres = genreRepository.findAllActive();
        
        // Manual pagination since repository doesn't have a paginated method
        int total = genres.size();
        int fromIndex = pageIndex * limit;
        int toIndex = Math.min(fromIndex + limit, total);
        
        List<Genre> pagedGenres = fromIndex < total ? 
                genres.subList(fromIndex, toIndex) : new ArrayList<>();
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", pagedGenres);
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", (int) Math.ceil((double) total / limit));
        response.put("total", total);
        
        return response;
    }

    /**
     * Get genre by ID
     */
    public Optional<Genre> getGenreById(UUID id) {
        return genreRepository.findById(id);
    }
    
    /**
     * Get active genre by ID
     */
    public Optional<Genre> getActiveGenreById(UUID id) {
        return genreRepository.findActiveById(id);
    }
    
    /**
     * Get genre by title (exact match, case insensitive)
     */
    public Optional<Genre> getGenreByTitle(String title) {
        return genreRepository.findByTitleIgnoreCase(title);
    }
    
    /**
     * Get active genre by title (exact match, case insensitive)
     */
    public Optional<Genre> getActiveGenreByTitle(String title) {
        return genreRepository.findActiveByTitle(title);
    }
    
    /**
     * Search genres by title (partial match)
     */
    public List<Genre> searchGenresByTitle(String title) {
        return genreRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Search active genres by title (partial match)
     */
    public List<Genre> searchActiveGenresByTitle(String title) {
        return genreRepository.findActiveByTitleContaining(title);
    }
    
    /**
     * Get all deleted genres
     */
    public List<Genre> getAllDeletedGenres() {
        return genreRepository.findAllDeleted();
    }
    
    /**
     * Get genres by artist ID
     */
    public List<Genre> getGenresByArtistId(UUID artistId) {
        return genreRepository.findByArtistId(artistId);
    }
    
    /**
     * Get active genres by artist ID
     */
    public List<Genre> getActiveGenresByArtistId(UUID artistId) {
        return genreRepository.findActiveByArtistId(artistId);
    }
    
    /**
     * Get most popular genres
     */
    public List<Genre> getMostPopularGenres(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return genreRepository.findMostPopularGenres(pageable);
    }

    /**
     * Create a new genre
     */
    @Transactional
    public Genre createGenre(String title) {
        // Check if genre with this title already exists
        Optional<Genre> existingGenre = genreRepository.findByTitleIgnoreCase(title);
        if (existingGenre.isPresent()) {
            throw new IllegalStateException("Genre with title '" + title + "' already exists");
        }
        
        Genre genre = new Genre();
        genre.setTitle(title);
        genre.setArtists(new HashSet<>());
        
        return genreRepository.save(genre);
    }
    
    /**
     * Update genre details
     */
    @Transactional
    public Optional<Genre> updateGenre(UUID genreId, String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre title cannot be empty");
        }
        
        // Check if another genre with this title already exists
        Optional<Genre> existingGenre = genreRepository.findByTitleIgnoreCase(title);
        if (existingGenre.isPresent() && !existingGenre.get().getId().equals(genreId)) {
            throw new IllegalStateException("Another genre with title '" + title + "' already exists");
        }
        
        return genreRepository.findActiveById(genreId).map(genre -> {
            genre.setTitle(title);
            return genreRepository.save(genre);
        });
    }
    
    /**
     * Soft delete a genre
     */
    @Transactional
    public boolean softDeleteGenre(UUID genreId) {
        return genreRepository.findActiveById(genreId).map(genre -> {
            genre.softDelete();
            genreRepository.save(genre);
            return true;
        }).orElse(false);
    }
    
    /**
     * Restore a soft-deleted genre
     */
    @Transactional
    public boolean restoreGenre(UUID genreId) {
        return genreRepository.findById(genreId)
                .filter(Genre::isDeleted)
                .map(genre -> {
                    genre.restore();
                    genreRepository.save(genre);
                    return true;
                }).orElse(false);
    }
    
    /**
     * Hard delete a genre (use with caution)
     */
    @Transactional
    public boolean hardDeleteGenre(UUID genreId) {
        if (genreRepository.existsById(genreId)) {
            genreRepository.deleteById(genreId);
            return true;
        }
        return false;
    }
    
    /**
     * Add a genre to an artist
     */
    @Transactional
    public Optional<Artist> addGenreToArtist(UUID genreId, UUID artistId) {
        Optional<Genre> genreOpt = genreRepository.findActiveById(genreId);
        Optional<Artist> artistOpt = artistRepository.findById(artistId);
        
        if (genreOpt.isPresent() && artistOpt.isPresent()) {
            Genre genre = genreOpt.get();
            Artist artist = artistOpt.get();
            
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
    public Optional<Artist> removeGenreFromArtist(UUID genreId, UUID artistId) {
        Optional<Genre> genreOpt = genreRepository.findById(genreId);
        Optional<Artist> artistOpt = artistRepository.findById(artistId);
        
        if (genreOpt.isPresent() && artistOpt.isPresent()) {
            Genre genre = genreOpt.get();
            Artist artist = artistOpt.get();
            
            artist.getGenres().remove(genre);
            genre.getArtists().remove(artist);
            
            genreRepository.save(genre);
            return Optional.of(artistRepository.save(artist));
        }
        
        return Optional.empty();
    }
    
    /**
     * Get number of artists in a genre
     */
    public int getArtistCountInGenre(UUID genreId) {
        return genreRepository.findById(genreId)
                .map(genre -> genre.getArtists().size())
                .orElse(0);
    }
    
    /**
     * Check if genre exists by title (case insensitive)
     */
    public boolean existsByTitle(String title) {
        return genreRepository.findByTitleIgnoreCase(title).isPresent();
    }
    
    /**
     * Check if active genre exists by title (case insensitive)
     */
    public boolean existsActiveByTitle(String title) {
        return genreRepository.findActiveByTitle(title).isPresent();
    }
    
    /**
     * Add multiple genres to an artist
     */
    @Transactional
    public Optional<Artist> addGenresToArtist(List<UUID> genreIds, UUID artistId) {
        Optional<Artist> artistOpt = artistRepository.findById(artistId);
        
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
     * Create genre if it doesn't exist and return it
     */
    @Transactional
    public Genre getOrCreateGenre(String title) {
        return genreRepository.findActiveByTitle(title)
                .orElseGet(() -> createGenre(title));
    }
}