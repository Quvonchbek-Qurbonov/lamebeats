package org.example.lamebeats.controllers;

import org.example.lamebeats.dto.SongDto;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.services.SongService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSongs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Map<String, Object> response = songService.getAllActiveSongsPaginated(page, limit);

        // Convert entities to DTOs
        List<Song> songs = (List<Song>) response.get("data");
        List<SongDto> songDtos = songs.stream()
                .map(SongDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", songDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSongById(@PathVariable String id) {
        UUID songId;
        try {
            songId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        return songService.getActiveSongById(songId)
                .map(song -> {
                    SongDto dto = SongDto.fromEntity(song);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSongs(@RequestParam String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Search term is required"));
        }

        List<Song> songs = songService.searchSongsByTitleOrArtist(searchTerm);
        List<SongDto> songDtos = songs.stream()
                .map(SongDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", songDtos));
    }

    @PostMapping
    public ResponseEntity<?> createSong(@RequestBody Map<String, String> payload) {
        // Validate required fields
        if (!payload.containsKey("title") || payload.get("title").trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Song title is required"));
        }

        UUID currentUserId = CurrentUser.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        String title = payload.get("title");
        String genre = payload.getOrDefault("genre", "");
        int duration = Integer.parseInt(payload.getOrDefault("duration", "0"));
        UUID albumId = UUID.fromString(payload.getOrDefault("albumId", ""));

        try {
            Song createdSong = songService.createSong(title, genre, duration, albumId);
            SongDto dto = SongDto.fromEntity(createdSong);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create song: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSong(@PathVariable String id, @RequestBody Map<String, String> payload) {
        UUID songId;
        try {
            songId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        // Get update data
        String title = payload.get("title");
        String genre = payload.get("genre");
        int duration = Integer.parseInt(payload.getOrDefault("duration", "0"));
        UUID albumId = UUID.fromString(payload.getOrDefault("albumId", ""));

        // Update song
        return songService.updateSong(songId, title, genre, duration, albumId)
                .map(song -> {
                    SongDto dto = SongDto.fromEntity(song);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable String id) {
        UUID songId;
        try {
            songId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        boolean deleted = songService.softDeleteSong(songId);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}