package org.example.lamebeats.controllers;

import org.example.lamebeats.dto.PlaylistDto;
import org.example.lamebeats.models.Playlist;
import org.example.lamebeats.services.PlaylistService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    private final PlaylistService playlistService;
    private final CurrentUser currentUser;

    @Autowired
    public PlaylistController(PlaylistService playlistService, CurrentUser currentUser) {
        this.playlistService = playlistService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPlaylists(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can access this endpoint"));
        }

        Map<String, Object> response = playlistService.getAllActivePlaylistsPaginated(page, limit);

        // Convert entities to DTOs
        List<Playlist> playlists = (List<Playlist>) response.get("data");
        List<PlaylistDto> playlistDtos = playlists.stream()
                .map(PlaylistDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", playlistDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUserPlaylists(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        UUID currentUserId = CurrentUser.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        Map<String, Object> response = playlistService.getActivePlaylistsByUserIdPaginated(currentUserId, page, limit);

        // Convert entities to DTOs
        List<Playlist> playlists = (List<Playlist>) response.get("data");
        List<PlaylistDto> playlistDtos = playlists.stream()
                .map(PlaylistDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", playlistDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPlaylists(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        UUID userUUID;
        try {
            userUUID = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid user ID format"));
        }

        Map<String, Object> response = playlistService.getActivePlaylistsByUserIdPaginated(userUUID, page, limit);

        // Convert entities to DTOs
        List<Playlist> playlists = (List<Playlist>) response.get("data");
        List<PlaylistDto> playlistDtos = playlists.stream()
                .map(PlaylistDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", playlistDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaylistById(@PathVariable String id) {
        UUID playlistId;
        try {
            playlistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid playlist ID format"));
        }

        return playlistService.getActivePlaylistById(playlistId)
                .map(playlist -> {
                    PlaylistDto dto = PlaylistDto.fromEntity(playlist);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(@RequestBody Map<String, String> payload) {
        // Validate required fields
        if (!payload.containsKey("name") || payload.get("name").trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Playlist name is required"));
        }

        UUID currentUserId = CurrentUser.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        String name = payload.get("name");
        String description = payload.getOrDefault("description", "");
        String photo = payload.getOrDefault("photo", "");

        try {
            Playlist createdPlaylist = playlistService.createPlaylist(name, description, photo, currentUserId);
            PlaylistDto dto = PlaylistDto.fromEntity(createdPlaylist);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create playlist: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlaylist(@PathVariable String id, @RequestBody Map<String, String> payload) {
        UUID playlistId;
        try {
            playlistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid playlist ID format"));
        }

        // Check if user owns this playlist
        UUID currentUserId = CurrentUser.getCurrentUserId();
        if (!playlistService.isPlaylistOwnedByUser(playlistId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You don't have permission to update this playlist"));
        }

        // Get update data
        String name = payload.get("name");
        String description = payload.get("description");
        String photo = payload.get("photo");

        // Update playlist
        return playlistService.updatePlaylist(playlistId, name, photo, description)
                .map(playlist -> {
                    PlaylistDto dto = PlaylistDto.fromEntity(playlist);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/songs")
    public ResponseEntity<?> addSongToPlaylist(@PathVariable String id, @RequestBody Map<String, String> payload) {
        UUID playlistId;
        try {
            playlistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid playlist ID format"));
        }

        // Check if song ID is provided
        if (!payload.containsKey("songId")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Song ID is required"));
        }

        UUID songId;
        try {
            songId = UUID.fromString(payload.get("songId"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        // Check if user owns this playlist
        UUID currentUserId = CurrentUser.getCurrentUserId();
        if (!playlistService.isPlaylistOwnedByUser(playlistId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You don't have permission to modify this playlist"));
        }

        return playlistService.addSongToPlaylist(playlistId, songId)
                .map(playlist -> {
                    PlaylistDto dto = PlaylistDto.fromEntity(playlist);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<?> removeSongFromPlaylist(@PathVariable String id, @PathVariable String songId) {
        UUID playlistId;
        UUID songUUID;

        try {
            playlistId = UUID.fromString(id);
            songUUID = UUID.fromString(songId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
        }

        // Check if user owns this playlist
        UUID currentUserId = CurrentUser.getCurrentUserId();
        if (!playlistService.isPlaylistOwnedByUser(playlistId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You don't have permission to modify this playlist"));
        }

        return playlistService.removeSongFromPlaylist(playlistId, songUUID)
                .map(playlist -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Playlist or song not found")));
    }

    @PostMapping("/{id}/songs/bulk")
    public ResponseEntity<?> addSongsToPlaylist(@PathVariable String id, @RequestBody Map<String, List<String>> payload) {
        UUID playlistId;
        try {
            playlistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid playlist ID format"));
        }

        // Check if song IDs are provided
        if (!payload.containsKey("songIds") || payload.get("songIds").isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Song IDs are required"));
        }

        // Convert string IDs to UUIDs
        List<UUID> songIds;
        try {
            songIds = payload.get("songIds").stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        // Check if user owns this playlist
        UUID currentUserId = CurrentUser.getCurrentUserId();
        if (!playlistService.isPlaylistOwnedByUser(playlistId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You don't have permission to modify this playlist"));
        }

        return playlistService.addSongsToPlaylist(playlistId, songIds)
                .map(playlist -> {
                    PlaylistDto dto = PlaylistDto.fromEntity(playlist);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/songs/bulk")
    public ResponseEntity<?> removeSongsFromPlaylist(@PathVariable String id, @RequestBody Map<String, List<String>> payload) {
        UUID playlistId;
        try {
            playlistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid playlist ID format"));
        }

        // Check if song IDs are provided
        if (!payload.containsKey("songIds") || payload.get("songIds").isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Song IDs are required"));
        }

        // Convert string IDs to UUIDs
        List<UUID> songIds;
        try {
            songIds = payload.get("songIds").stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        // Check if user owns this playlist
        UUID currentUserId = CurrentUser.getCurrentUserId();
        if (!playlistService.isPlaylistOwnedByUser(playlistId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You don't have permission to modify this playlist"));
        }

        return playlistService.removeSongsFromPlaylist(playlistId, songIds)
                .map(playlist -> {
                    PlaylistDto dto = PlaylistDto.fromEntity(playlist);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/songs/all")
    public ResponseEntity<?> clearPlaylist(@PathVariable String id) {
        UUID playlistId;
        try {
            playlistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid playlist ID format"));
        }

        // Check if user owns this playlist
        UUID currentUserId = CurrentUser.getCurrentUserId();
        if (!playlistService.isPlaylistOwnedByUser(playlistId, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You don't have permission to modify this playlist"));
        }

        return playlistService.clearPlaylist(playlistId)
                .map(playlist -> {
                    PlaylistDto dto = PlaylistDto.fromEntity(playlist);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPlaylists(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Search term is required"));
        }

        List<Playlist> playlists = playlistService.searchActivePlaylistsByName(name);
        List<PlaylistDto> playlistDtos = playlists.stream()
                .map(PlaylistDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", playlistDtos));
    }
}