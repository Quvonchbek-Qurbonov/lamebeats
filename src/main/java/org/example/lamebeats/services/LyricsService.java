package org.example.lamebeats.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.lamebeats.enums.Language;
import org.example.lamebeats.models.Lyrics;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.repositories.LyricsRepository;
import org.example.lamebeats.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LyricsService {

    private final LyricsRepository lyricsRepository;
    private final SongRepository songRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public LyricsService(LyricsRepository lyricsRepository, SongRepository songRepository, ObjectMapper objectMapper) {
        this.lyricsRepository = lyricsRepository;
        this.songRepository = songRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Get all lyrics (regardless of deleted status)
     */
    public List<Lyrics> getAllLyrics() {
        return lyricsRepository.findAll();
    }

    /**
     * Get all active lyrics
     */
    public List<Lyrics> getAllActiveLyrics() {
        return lyricsRepository.findAllActive();
    }

    /**
     * Get all active lyrics with pagination
     */
    public Map<String, Object> getAllActiveLyricsPaginated(int page, int limit) {
        int pageIndex = Math.max(page - 1, 0); // Convert to zero-based index

        List<Lyrics> lyrics = lyricsRepository.findAllActive();

        // Manual pagination since repository doesn't have a paginated method
        int total = lyrics.size();
        int fromIndex = pageIndex * limit;
        int toIndex = Math.min(fromIndex + limit, total);

        List<Lyrics> pagedLyrics = fromIndex < total ?
                lyrics.subList(fromIndex, toIndex) : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("data", pagedLyrics);
        response.put("page", page);
        response.put("limit", limit);
        response.put("pages", (int) Math.ceil((double) total / limit));
        response.put("total", total);

        return response;
    }

    /**
     * Get lyrics by ID
     */
    public Optional<Lyrics> getLyricsById(UUID id) {
        return lyricsRepository.findById(id);
    }

    /**
     * Get active lyrics by ID
     */
    public Optional<Lyrics> getActiveLyricsById(UUID id) {
        return lyricsRepository.findActiveById(id);
    }

    /**
     * Get lyrics by song ID
     */
    public List<Lyrics> getLyricsBySongId(UUID songId) {
        return lyricsRepository.findBySongId(songId);
    }

    /**
     * Get active lyrics by song ID
     */
    public List<Lyrics> getActiveLyricsBySongId(UUID songId) {
        return lyricsRepository.findActiveBySongId(songId);
    }

    /**
     * Get lyrics by song ID and language
     */
    public Optional<Lyrics> getLyricsBySongIdAndLanguage(UUID songId, Language language) {
        return lyricsRepository.findBySongIdAndLanguage(songId, language);
    }

    /**
     * Get active lyrics by song ID and language
     */
    public Optional<Lyrics> getActiveLyricsBySongIdAndLanguage(UUID songId, Language language) {
        return lyricsRepository.findActiveBySongIdAndLanguage(songId, language);
    }

    /**
     * Get lyrics by language
     */
    public List<Lyrics> getLyricsByLanguage(Language language) {
        return lyricsRepository.findByLanguage(language);
    }

    /**
     * Get active lyrics by language
     */
    public List<Lyrics> getActiveLyricsByLanguage(Language language) {
        return lyricsRepository.findActiveByLanguage(language);
    }

    /**
     * Get all deleted lyrics
     */
    public List<Lyrics> getAllDeletedLyrics() {
        return lyricsRepository.findAllDeleted();
    }

    /**
     * Check if lyrics exist for a song
     */
    public boolean existsLyricsForSong(UUID songId) {
        return lyricsRepository.existsBySongId(songId);
    }

    /**
     * Check if lyrics exist for a song in a specific language
     */
    public boolean existsLyricsForSongInLanguage(UUID songId, Language language) {
        return lyricsRepository.existsBySongIdAndLanguage(songId, language);
    }

    /**
     * Check if active lyrics exist for a song in a specific language
     */
    public boolean existsActiveLyricsForSongInLanguage(UUID songId, Language language) {
        return lyricsRepository.existsActiveBySongIdAndLanguage(songId, language);
    }

    /**
     * Create new lyrics
     */
    @Transactional
    public Lyrics createLyrics(UUID songId, String content, Language language) {
        // Check if song exists
        Optional<Song> songOpt = songRepository.findById(songId);
        if (songOpt.isEmpty()) {
            throw new IllegalArgumentException("Song with ID " + songId + " not found");
        }

        Song song = songOpt.get();

        // Check if lyrics in this language already exist for the song
        if (lyricsRepository.existsActiveBySongIdAndLanguage(songId, language)) {
            throw new IllegalArgumentException("Lyrics for song in language " + language + " already exist");
        }

        // Convert plain text content to JSON structure
        String jsonContent = convertToJsonFormat(content);

        Lyrics lyrics = new Lyrics();
        lyrics.setSong(song);
        lyrics.setContent(jsonContent);
        lyrics.setLanguage(language);

        return lyricsRepository.save(lyrics);
    }

    /**
     * Convert plain text lyrics to proper JSON format for JSONB column
     */
    /**
     * Convert plain text lyrics to proper JSON format for JSONB column
     */
    private String convertToJsonFormat(String plainText) {
        try {
            // Split the lyrics into individual lines
            String[] lines = plainText.split("\n");

            // Create a list to store the lyrics lines (excluding copyright)
            List<String> lyricLines = new ArrayList<>();

            // Process each line, excluding copyright notice
            for (String line : lines) {
                // Skip copyright lines and the ID line
                if (!line.contains("*******") && !line.matches("\\(\\d+\\)")) {
                    lyricLines.add(line);
                }
            }

            // Create the final structure
            Map<String, Object> root = new HashMap<>();
            root.put("lyrics", lyricLines);

            // Convert to JSON string
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            // In case of any errors, create a simple JSON with the full text
            try {
                // Remove copyright lines from the fallback text
                String[] lines = plainText.split("\n");
                List<String> cleanedLines = new ArrayList<>();

                for (String line : lines) {
                    if (!line.contains("*******") && !line.matches("\\(\\d+\\)")) {
                        cleanedLines.add(line.replace("\\", "\\\\").replace("\"", "\\\""));
                    }
                }

                StringBuilder jsonBuilder = new StringBuilder("{\"lyrics\":[");
                for (int i = 0; i < cleanedLines.size(); i++) {
                    if (i > 0) {
                        jsonBuilder.append(",");
                    }
                    jsonBuilder.append("\"").append(cleanedLines.get(i)).append("\"");
                }
                jsonBuilder.append("]}");

                return jsonBuilder.toString();
            } catch (Exception ex) {
                throw new RuntimeException("Failed to convert lyrics to JSON format", ex);
            }
        }
    }

    /**
     * Update lyrics
     */
    @Transactional
    public Optional<Lyrics> updateLyrics(UUID lyricsId, String content, Language language) {
        return lyricsRepository.findActiveById(lyricsId).map(lyrics -> {
            if (content != null && !content.trim().isEmpty()) {
                // Convert plain text content to JSON structure
                String jsonContent = convertToJsonFormat(content);
                lyrics.setContent(jsonContent);
            }

            if (language != null) {
                // Check if changing language would create a duplicate
                if (!lyrics.getLanguage().equals(language) &&
                        lyricsRepository.existsActiveBySongIdAndLanguage(lyrics.getSong().getId(), language)) {
                    throw new IllegalArgumentException("Lyrics for this song in language " + language + " already exist");
                }
                lyrics.setLanguage(language);
            }

            return lyricsRepository.save(lyrics);
        });
    }

    /**
     * Soft delete lyrics
     */
    @Transactional
    public boolean softDeleteLyrics(UUID lyricsId) {
        return lyricsRepository.findActiveById(lyricsId).map(lyrics -> {
            lyrics.softDelete();
            lyricsRepository.save(lyrics);
            return true;
        }).orElse(false);
    }

    /**
     * Restore soft-deleted lyrics
     */
    @Transactional
    public boolean restoreLyrics(UUID lyricsId) {
        return lyricsRepository.findById(lyricsId)
                .filter(lyrics -> lyrics.getDeletedAt() != null)
                .map(lyrics -> {
                    // Check if restoring would create a duplicate
                    if (lyricsRepository.existsActiveBySongIdAndLanguage(lyrics.getSong().getId(), lyrics.getLanguage())) {
                        throw new IllegalArgumentException(
                                "Cannot restore: Active lyrics already exist for this song in language " + lyrics.getLanguage());
                    }

                    lyrics.restore();
                    lyricsRepository.save(lyrics);
                    return true;
                }).orElse(false);
    }

    /**
     * Hard delete lyrics (use with caution)
     */
    @Transactional
    public boolean hardDeleteLyrics(UUID lyricsId) {
        if (lyricsRepository.existsById(lyricsId)) {
            lyricsRepository.deleteById(lyricsId);
            return true;
        }
        return false;
    }
}