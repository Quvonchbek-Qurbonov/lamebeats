package org.example.lamebeats.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.lamebeats.dto.LyricsDto;
import org.example.lamebeats.dto.SongDto;
import org.example.lamebeats.enums.Language;
import org.example.lamebeats.models.Lyrics;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.services.LyricsService;
import org.example.lamebeats.services.SongService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lyrics")
public class LyricsController {
    private final LyricsService lyricsService;
    private final CurrentUser currentUser;
    private final SongService songService;

    @Value("${musixmatch.apikey}")
    private String musixmatchApiKey;

    @Autowired
    public LyricsController(LyricsService lyricsService, CurrentUser currentUser, SongService songService) {
        this.lyricsService = lyricsService;
        this.currentUser = currentUser;
        this.songService = songService;
    }

    private String fetchLyricsFromMusixmatch(String commontrack_id) {
        String url = "https://api.musixmatch.com/ws/1.1/track.lyrics.get?apikey=" + musixmatchApiKey + "&commontrack_id=" + commontrack_id;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLyrics(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {

        Map<String, Object> response = lyricsService.getAllActiveLyricsPaginated(page, limit);

        // Convert entities to DTOs
        List<Lyrics> lyrics = (List<Lyrics>) response.get("data");
        List<LyricsDto> lyricsDtos = lyrics.stream()
                .map(LyricsDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", lyricsDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllLyricsIncludingDeleted() {
        // Only admins can see deleted lyrics
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can access this endpoint"));
        }

        List<Lyrics> lyrics = lyricsService.getAllLyrics();
        List<LyricsDto> lyricsDtos = lyrics.stream()
                .map(LyricsDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", lyricsDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLyricsById(@PathVariable String id) {
        UUID lyricsId;
        try {
            lyricsId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid lyrics ID format"));
        }

        return lyricsService.getActiveLyricsById(lyricsId)
                .map(lyrics -> {
                    LyricsDto dto = LyricsDto.fromEntity(lyrics);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/song/{songId}")
    public ResponseEntity<?> getLyricsBySong(@PathVariable String songId) {
        UUID songUUID;
        try {
            songUUID = UUID.fromString(songId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        List<Lyrics> lyrics = lyricsService.getActiveLyricsBySongId(songUUID);
        List<LyricsDto> lyricsDtos = lyrics.stream()
                .map(LyricsDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", lyricsDtos));
    }

    @GetMapping("/song/{songId}/language/{language}")
    public ResponseEntity<?> getLyricsBySongAndLanguage(
            @PathVariable String songId,
            @PathVariable String language) {

        UUID songUUID;
        try {
            songUUID = UUID.fromString(songId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        Language lang;
        try {
            lang = Language.valueOf(language.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid language. Valid values are: " +
                    Arrays.toString(Language.values())));
        }

        return lyricsService.getActiveLyricsBySongIdAndLanguage(songUUID, lang)
                .map(lyrics -> {
                    LyricsDto dto = LyricsDto.fromEntity(lyrics);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<?> getLyricsByLanguage(@PathVariable String language) {
        Language lang;
        try {
            lang = Language.valueOf(language.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid language. Valid values are: " +
                    Arrays.toString(Language.values())));
        }

        List<Lyrics> lyrics = lyricsService.getActiveLyricsByLanguage(lang);
        List<LyricsDto> lyricsDtos = lyrics.stream()
                .map(LyricsDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", lyricsDtos));
    }

    @PostMapping
    public ResponseEntity<?> createLyrics(@RequestParam String songId) {
        // Call the SongController's getSongById method to get song information
        UUID id;
        try {
            id = UUID.fromString(songId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        Optional<Song> activeSongById = songService.getActiveSongById(id);

        // Extract the title and artist name
        String title = activeSongById.get().getTitle();
        String artistName = "";

        // Get the first artist's name if available
        if (activeSongById.get().getArtists() != null && !activeSongById.get().getArtists().isEmpty()) {
            artistName = activeSongById.get().getArtists().iterator().next().getName();
        }

        // Check if we have enough information to query Musixmatch
        if (title.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Song title is required"));
        }

        try {
            // Create a RestTemplate for making the API request
            RestTemplate restTemplate = new RestTemplate();

            // Step 1: Get the commontrack_id
            String matcherUrl = String.format("%s?apikey=%s&q_track=%s&q_artist=%s",
                    "https://api.musixmatch.com/ws/1.1/matcher.track.get",
                    musixmatchApiKey,
                    title,
                    artistName);

            // Make the first API request to get track info
            String matcherResponse = restTemplate.getForObject(
                    matcherUrl,
                    String.class
            );

            // Parse the JSON response to extract commontrack_id
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(matcherResponse);

            // Navigate through the nested structure to get the commontrack_id
            long commontrackId = rootNode
                    .path("message")
                    .path("body")
                    .path("track")
                    .path("commontrack_id")
                    .asLong();

            // Step 2: Use the commontrack_id to get lyrics
            String lyricsUrl = String.format("%s?apikey=%s&commontrack_id=%d",
                    "https://api.musixmatch.com/ws/1.1/track.lyrics.get",
                    musixmatchApiKey,
                    commontrackId);

            // Make the second API request to get lyrics
            String lyricsResponse = restTemplate.getForObject(
                    lyricsUrl,
                    String.class
            );

            // Parse the lyrics response to extract the lyrics text
            JsonNode lyricsRootNode = objectMapper.readTree(lyricsResponse);
            String lyricsBody = lyricsRootNode
                    .path("message")
                    .path("body")
                    .path("lyrics")
                    .path("lyrics_body")
                    .asText();

            // Process the lyrics:
            // 1. Split into lines
            // 2. Remove the copyright notice at the end (usually starts with "...")
            // 3. Remove any empty lines
            List<String> lyricsLines = new ArrayList<>();
            String[] lines = lyricsBody.split("\n");

            for (String line : lines) {
                // Skip copyright line (usually starts with "...")
                if (line.trim().startsWith("...") ||
                        line.contains("This Lyrics is NOT for Commercial use")) {
                    break;
                }

                // Add non-empty lines without redundant backslashes
                String cleanLine = line.trim().replace("\\", "");
                if (!cleanLine.isEmpty()) {
                    lyricsLines.add(cleanLine);
                }
            }

            // Create a JSON structure manually before saving to DB
            // This way we control the exact format without unwanted escaping
            Map<String, List<String>> lyricsMap = new HashMap<>();
            lyricsMap.put("lyrics", lyricsLines);

            // Convert to JSON string with clean formatting
            String lyricsJson = objectMapper.writeValueAsString(lyricsMap);

            // Save the lyrics to the database using the service
            UUID songUUID = UUID.fromString(songId);

            // Default to English language
            Lyrics savedLyrics = lyricsService.createLyrics(songUUID, lyricsJson, Language.UNKNOWN);

            // Create the response with the formatted lyrics and database details
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedLyrics.getId().toString());
            response.put("songId", songId);
            response.put("songTitle", title);
            response.put("lyrics", lyricsLines);
            response.put("language", savedLyrics.getLanguage().toString());
            response.put("createdAt", savedLyrics.getCreatedAt().toString());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // This likely means lyrics already exist for this song
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", e.getMessage(),
                            "title", title,
                            "artist", artistName
                    ));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to parse Musixmatch response: " + e.getMessage(),
                            "title", title,
                            "artist", artistName
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to fetch or save lyrics: " + e.getMessage(),
                            "title", title,
                            "artist", artistName
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLyrics(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        // Only admins can update lyrics
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can update lyrics"));
        }

        UUID lyricsId;
        try {
            lyricsId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid lyrics ID format"));
        }

        // Extract fields to update
        String content = payload.containsKey("content") ? payload.get("content").toString() : null;

        Language language = null;
        if (payload.containsKey("language") && payload.get("language") != null) {
            try {
                language = Language.valueOf(payload.get("language").toString().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid language. Valid values are: " +
                        Arrays.toString(Language.values())));
            }
        }

        try {
            Optional<Lyrics> updatedLyricsOpt = lyricsService.updateLyrics(lyricsId, content, language);

            return updatedLyricsOpt
                    .map(lyrics -> {
                        LyricsDto dto = LyricsDto.fromEntity(lyrics);
                        return ResponseEntity.ok(dto);
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update lyrics: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLyrics(@PathVariable String id) {
        // Only admins can delete lyrics
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can delete lyrics"));
        }

        UUID lyricsId;
        try {
            lyricsId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid lyrics ID format"));
        }

        boolean deleted = lyricsService.softDeleteLyrics(lyricsId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Lyrics deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreLyrics(@PathVariable String id) {
        // Only admins can restore lyrics
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can restore lyrics"));
        }

        UUID lyricsId;
        try {
            lyricsId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid lyrics ID format"));
        }

        boolean restored = lyricsService.restoreLyrics(lyricsId);
        if (restored) {
            return ResponseEntity.ok(Map.of("message", "Lyrics restored successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<?> permanentlyDeleteLyrics(@PathVariable String id) {
        // Only admins can permanently delete lyrics
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can permanently delete lyrics"));
        }

        UUID lyricsId;
        try {
            lyricsId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid lyrics ID format"));
        }

        boolean deleted = lyricsService.hardDeleteLyrics(lyricsId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Lyrics permanently deleted"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}