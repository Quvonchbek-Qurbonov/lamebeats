package org.example.lamebeats.controllers;

import org.example.lamebeats.dto.AlbumDto;
import org.example.lamebeats.models.Album;
import org.example.lamebeats.services.AlbumService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    private final AlbumService albumService;
    private final CurrentUser currentUser;

    @Autowired
    public AlbumController(AlbumService albumService, CurrentUser currentUser) { // Modify constructor
        this.albumService = albumService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAlbums(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Map<String, Object> response = albumService.getAllActiveAlbumsPaginated(page, limit);

        // Convert entities to DTOs
        List<Album> albums = (List<Album>) response.get("data");
        List<AlbumDto> albumDtos = albums.stream()
                .map(AlbumDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", albumDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlbumById(@PathVariable String id) {
        UUID albumId;
        try {
            albumId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid album ID format"));
        }

        return albumService.getActiveAlbumById(albumId)
                .map(album -> {
                    AlbumDto dto = AlbumDto.fromEntity(album);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAlbums(@RequestParam String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Search term is required"));
        }

        List<Album> albums = albumService.searchAlbumsByTitle(searchTerm);
        List<AlbumDto> albumDtos = albums.stream()
                .map(AlbumDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", albumDtos));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<?> getAlbumsByArtist(@PathVariable String artistId) {
        UUID artistUUID;
        try {
            artistUUID = UUID.fromString(artistId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
        }

        List<Album> albums = albumService.getActiveAlbumsByArtistId(artistUUID);
        List<AlbumDto> albumDtos = albums.stream()
                .map(AlbumDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", albumDtos));
    }

    @PostMapping
    public ResponseEntity<?> createAlbum(@RequestBody Map<String, String> payload) {
        // Validate required fields
        if (!payload.containsKey("title") || payload.get("title").trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Album title is required"));
        }

        UUID currentUserId = CurrentUser.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        String title = payload.get("title");
        LocalDate releaseDate = LocalDate.parse(payload.getOrDefault("releaseDate", LocalDate.now().toString()));
        String photo = payload.getOrDefault("photo", "");

        try {
            Album createdAlbum = albumService.createAlbum(title, releaseDate, photo, currentUserId);
            AlbumDto dto = AlbumDto.fromEntity(createdAlbum);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create album: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAlbum(@PathVariable String id, @RequestBody Map<String, String> payload) {
        UUID albumId;
        try {
            albumId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid album ID format"));
        }

        // Get update data
        String title = payload.get("title");
        LocalDate releaseDate = LocalDate.parse(payload.getOrDefault("releaseDate", LocalDate.now().toString()));
        String photo = payload.get("photo");

        // Update album
        return albumService.updateAlbum(albumId, title, releaseDate, photo)
                .map(album -> {
                    AlbumDto dto = AlbumDto.fromEntity(album);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlbum(@PathVariable String id) {
        UUID albumId;
        try {
            albumId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid album ID format"));
        }

        boolean deleted = albumService.softDeleteAlbum(albumId);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}