package org.example.lamebeats.services;

import org.example.lamebeats.models.Album;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.repositories.AlbumRepository;
import org.example.lamebeats.repositories.ArtistRepository;
import org.example.lamebeats.repositories.SongArtistRepository;
import org.example.lamebeats.repositories.SongRepository;
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
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongArtistRepository songArtistRepository;

    @Autowired
    public SongService(SongRepository songRepository,
                       ArtistRepository artistRepository,
                       AlbumRepository albumRepository,
                       SongArtistRepository songArtistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.songArtistRepository = songArtistRepository;
    }

    /**
     * Helper method to ensure artists are loaded for a list of songs
     */
    private List<Song> ensureArtistsLoaded(List<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            return songs;
        }

        List<UUID> ids = songs.stream()
                .map(Song::getId)
                .collect(Collectors.toList());

        return songRepository.findByIdInWithArtists(ids);
    }

    /**
     * Get all songs (regardless of deleted status)
     */
    @Transactional(readOnly = true)
    public List<Song> getAllSongs() {
        List<Song> songs = songRepository.findAll();
        List<UUID> songIds = songs.stream().map(Song::getId).collect(Collectors.toList());

        if (!songIds.isEmpty()) {
            return songRepository.findByIdInWithArtists(songIds);
        }
        return songs;
    }

    /**
     * Get all active songs
     */
    @Transactional(readOnly = true)
    public List<Song> getAllActiveSongs() {
        return songRepository.findAllActive();
    }

    /**
     * Get all active songs with pagination
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAllActiveSongsPaginated(int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("createdAt").descending());

        Page<Song> songPage = songRepository.findAllActive(pageable);

        // Load artists for all songs in the page
        List<Song> songsWithArtists = songRepository.findByIdInWithArtists(
                songPage.getContent().stream().map(Song::getId).collect(Collectors.toList()));

        // Create a map for quick lookup
        Map<UUID, Song> songMap = songsWithArtists.stream()
                .collect(Collectors.toMap(Song::getId, song -> song));

        // Replace songs in the result with the ones that have artists loaded
        List<Song> resultSongs = songPage.getContent().stream()
                .map(song -> songMap.getOrDefault(song.getId(), song))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", resultSongs);
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", songPage.getTotalPages());
        response.put("total", songPage.getTotalElements());

        return response;
    }

    /**
     * Get song by ID
     */
    @Transactional(readOnly = true)
    public Optional<Song> getSongById(UUID id) {
        return songRepository.findById(id)
                .map(song -> {
                    // Use the repository method that joins artists
                    return songRepository.findByIdInWithArtists(Collections.singletonList(song.getId()))
                            .stream().findFirst().orElse(song);
                });
    }

    /**
     * Get active song by ID
     */
    @Transactional(readOnly = true)
    public Optional<Song> getActiveSongById(UUID id) {
        return songRepository.findActiveById(id);
    }

    /**
     * Search songs by title
     */
    @Transactional(readOnly = true)
    public List<Song> searchSongsByTitle(String title) {
        return songRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Search active songs by title
     */
    @Transactional(readOnly = true)
    public List<Song> searchActiveSongsByTitle(String title) {
        return songRepository.findActiveByTitleContaining(title);
    }

    /**
     * Search songs by title or artist name
     */
    @Transactional(readOnly = true)
    public List<Song> searchSongsByTitleOrArtist(String searchTerm) {
        return songRepository.searchByTitleOrArtist(searchTerm);
    }

    /**
     * Get songs by duration range
     */
    @Transactional(readOnly = true)
    public List<Song> getSongsByDurationRange(int minDuration, int maxDuration) {
        return ensureArtistsLoaded(songRepository.findByDurationBetween(minDuration, maxDuration));
    }

    /**
     * Get songs by album
     */
    @Transactional(readOnly = true)
    public List<Song> getSongsByAlbum(Album album) {
        return songRepository.findByAlbum(album);
    }

    /**
     * Get songs by album ID
     */
    @Transactional(readOnly = true)
    public List<Song> getSongsByAlbumId(UUID albumId) {
        return songRepository.findByAlbumId(albumId);
    }

    /**
     * Get active songs by album
     */
    @Transactional(readOnly = true)
    public List<Song> getActiveSongsByAlbum(Album album) {
        return songRepository.findActiveByAlbum(album);
    }

    /**
     * Get active songs by album ID
     */
    @Transactional(readOnly = true)
    public List<Song> getActiveSongsByAlbumId(UUID albumId) {
        return songRepository.findActiveByAlbumId(albumId);
    }

    /**
     * Get songs by artist
     */
    @Transactional(readOnly = true)
    public List<Song> getSongsByArtist(Artist artist) {
        return songRepository.findByArtist(artist);
    }

    /**
     * Get songs by artist ID
     */
    @Transactional(readOnly = true)
    public List<Song> getSongsByArtistId(UUID artistId) {
        return songRepository.findByArtistId(artistId);
    }

    /**
     * Get active songs by artist
     */
    @Transactional(readOnly = true)
    public List<Song> getActiveSongsByArtist(Artist artist) {
        return songRepository.findActiveByArtist(artist);
    }

    /**
     * Get active songs by artist ID
     */
    @Transactional(readOnly = true)
    public List<Song> getActiveSongsByArtistId(UUID artistId) {
        return songRepository.findActiveByArtistId(artistId);
    }

    /**
     * Get recently added songs
     */
    @Transactional(readOnly = true)
    public List<Song> getRecentlyAddedSongs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return songRepository.findRecentlyAddedSongs(pageable);
    }

    /**
     * Get all deleted songs
     */
    @Transactional(readOnly = true)
    public List<Song> getAllDeletedSongs() {
        return songRepository.findAllDeleted();
    }

    /**
     * Get songs not in a specific playlist
     */
    @Transactional(readOnly = true)
    public List<Song> getSongsNotInPlaylist(UUID playlistId) {
        return songRepository.findSongsNotInPlaylist(playlistId);
    }

    /**
     * Count artists for song
     */
    public long countArtistsForSong(UUID songId) {
        return songArtistRepository.countArtistsBySongId(songId);
    }

    /**
     * Count songs for artist
     */
    public long countSongsForArtist(UUID artistId) {
        return songArtistRepository.countSongsByArtistId(artistId);
    }

    /**
     * Get artist collaborations
     */
    public List<Object[]> getArtistCollaborations(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return songArtistRepository.findArtistCollaborations(pageable);
    }

    /**
     * Create a new song
     */
    @Transactional
    public Song createSong(String title, Integer duration, String fileUrl, UUID albumId, List<UUID> artistIds) {
        Song song = new Song();
        song.setTitle(title);
        song.setDuration(duration);
        song.setFileUrl(fileUrl);

        // Set album if provided
        if (albumId != null) {
            albumRepository.findById(albumId).ifPresent(song::setAlbum);
        }

        // Initialize artists set
        song.setArtists(new HashSet<>());

        Song savedSong = songRepository.save(song);

        // Add artists if provided
        if (artistIds != null && !artistIds.isEmpty()) {
            addArtistsToSong(savedSong.getId(), artistIds);
            // Reload song to get the updated artist relationships
            return songRepository.findById(savedSong.getId())
                    .flatMap(s -> songRepository.findActiveById(s.getId()))
                    .orElse(savedSong);
        }

        return savedSong;
    }

    /**
     * Update song details
     */
    @Transactional
    public Optional<Song> updateSong(UUID songId, String title, Integer duration, String genre, String fileUrl, UUID albumId) {
        return songRepository.findActiveById(songId).map(song -> {
            if (title != null && !title.trim().isEmpty()) {
                song.setTitle(title);
            }

            if (duration != null) {
                song.setDuration(duration);
            }

            if (fileUrl != null) {
                song.setFileUrl(fileUrl);
            }

            // Update album if provided
            if (albumId != null) {
                albumRepository.findById(albumId).ifPresent(song::setAlbum);
            } else if (albumId == null && song.getAlbum() != null) {
                // Remove album association if albumId is explicitly set to null
                song.setAlbum(null);
            }

            Song savedSong = songRepository.save(song);

            // Reload to ensure artists are loaded
            return songRepository.findActiveById(savedSong.getId()).orElse(savedSong);
        });
    }

    /**
     * Add artists to a song
     */
    @Transactional
    public Optional<Song> addArtistsToSong(UUID songId, List<UUID> artistIds) {
        Optional<Song> songOpt = songRepository.findActiveById(songId);

        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            List<Artist> artists = artistRepository.findAllById(artistIds);

            for (Artist artist : artists) {
                song.getArtists().add(artist);
                artist.getSongs().add(song);
            }

            Song savedSong = songRepository.save(song);

            // Reload to ensure artists are loaded
            return songRepository.findActiveById(savedSong.getId());
        }

        return Optional.empty();
    }

    /**
     * Remove artists from a song
     */
    @Transactional
    public Optional<Song> removeArtistsFromSong(UUID songId, List<UUID> artistIds) {
        Optional<Song> songOpt = songRepository.findActiveById(songId);

        if (songOpt.isPresent()) {
            Song song = songOpt.get();

            song.getArtists().removeIf(artist -> artistIds.contains(artist.getId()));

            // For each artist, also update the bidirectional relationship
            List<Artist> artists = artistRepository.findAllById(artistIds);
            for (Artist artist : artists) {
                artist.getSongs().remove(song);
                artistRepository.save(artist);
            }

            Song savedSong = songRepository.save(song);

            // Reload to ensure artists are loaded
            return songRepository.findActiveById(savedSong.getId());
        }

        return Optional.empty();
    }

    /**
     * Set artists for a song (replacing existing ones)
     */
    @Transactional
    public Optional<Song> setArtistsForSong(UUID songId, List<UUID> artistIds) {
        Optional<Song> songOpt = songRepository.findActiveById(songId);

        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            Set<Artist> currentArtists = new HashSet<>(song.getArtists());

            // Remove song from all current artists
            for (Artist artist : currentArtists) {
                artist.getSongs().remove(song);
                artistRepository.save(artist);
            }

            // Clear current artists
            song.getArtists().clear();

            // Add new artists
            if (artistIds != null && !artistIds.isEmpty()) {
                List<Artist> newArtists = artistRepository.findAllById(artistIds);

                for (Artist artist : newArtists) {
                    song.getArtists().add(artist);
                    artist.getSongs().add(song);
                    artistRepository.save(artist);
                }
            }

            Song savedSong = songRepository.save(song);

            // Reload to ensure artists are loaded
            return songRepository.findActiveById(savedSong.getId());
        }

        return Optional.empty();
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

    /**
     * Restore a soft-deleted song
     */
    @Transactional
    public boolean restoreSong(UUID songId) {
        return songRepository.findById(songId)
                .filter(song -> song.getDeletedAt() != null)
                .map(song -> {
                    song.setDeletedAt(null);
                    songRepository.save(song);
                    return true;
                }).orElse(false);
    }

    /**
     * Hard delete a song (use with caution)
     */
    @Transactional
    public boolean hardDeleteSong(UUID songId) {
        if (songRepository.existsById(songId)) {
            songRepository.deleteById(songId);
            return true;
        }
        return false;
    }

    /**
     * Get songs by genre ID (through artist-genre relationship)
     */
    @Transactional(readOnly = true)
    public List<Song> getSongsByGenreId(UUID genreId) {
        return songRepository.findSongsByGenreId(genreId);
    }

    /**
     * Get songs by multiple genre IDs (through artist-genre relationship)
     */
    @Transactional(readOnly = true)
    public List<Song> getSongsByGenreIds(Set<UUID> genreIds) {
        return songRepository.findSongsByGenreIds(genreIds);
    }

    /**
     * Get songs by genre ID with pagination
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSongsByGenreIdPaginated(UUID genreId, int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("title").ascending());

        Page<Song> songPage = songRepository.findSongsByGenreId(genreId, pageable);

        // Load artists for all songs in the page
        List<Song> songsWithArtists = songRepository.findByIdInWithArtists(
                songPage.getContent().stream().map(Song::getId).collect(Collectors.toList()));

        // Create a map for quick lookup
        Map<UUID, Song> songMap = songsWithArtists.stream()
                .collect(Collectors.toMap(Song::getId, song -> song));

        // Replace songs in the result with the ones that have artists loaded
        List<Song> resultSongs = songPage.getContent().stream()
                .map(song -> songMap.getOrDefault(song.getId(), song))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", resultSongs);
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", songPage.getTotalPages());
        response.put("total", songPage.getTotalElements());

        return response;
    }

    /**
     * Get songs by multiple genre IDs with pagination
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSongsByGenreIdsPaginated(Set<UUID> genreIds, int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index
        Pageable pageable = PageRequest.of(pageIndex, limit, Sort.by("title").ascending());

        Page<Song> songPage = songRepository.findSongsByGenreIds(genreIds, pageable);

        // Load artists for all songs in the page
        List<Song> songsWithArtists = songRepository.findByIdInWithArtists(
                songPage.getContent().stream().map(Song::getId).collect(Collectors.toList()));

        // Create a map for quick lookup
        Map<UUID, Song> songMap = songsWithArtists.stream()
                .collect(Collectors.toMap(Song::getId, song -> song));

        // Replace songs in the result with the ones that have artists loaded
        List<Song> resultSongs = songPage.getContent().stream()
                .map(song -> songMap.getOrDefault(song.getId(), song))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", resultSongs);
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", songPage.getTotalPages());
        response.put("total", songPage.getTotalElements());

        return response;
    }
}