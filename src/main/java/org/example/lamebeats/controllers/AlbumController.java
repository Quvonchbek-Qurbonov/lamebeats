package org.example.lamebeats.controllers;

import org.example.lamebeats.dto.AlbumDto;
import org.example.lamebeats.models.Album;
import org.example.lamebeats.services.AlbumService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    public AlbumController(AlbumService albumService, CurrentUser currentUser) {
        this.albumService = albumService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAlbums(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {

        Map<String, Object> response = albumService.getAllActiveAlbumsPaginated(page, limit);

        // Convert entities to DTOs
        List<Album> albums = (List<Album>) response.get("data");
        List<AlbumDto> albumDtos = albums.stream()
                .map(AlbumDto::fromEntitySimple)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", albumDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAlbumsIncludingDeleted() {
        // Only admins can see deleted albums
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can access this endpoint"));
        }

        List<Album> albums = albumService.getAllAlbums();
        List<AlbumDto> albumDtos = albums.stream()
                .map(AlbumDto::fromEntitySimple)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", albumDtos));
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
    public ResponseEntity<?> searchAlbums(@RequestParam String title) {
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Search term is required"));
        }

        List<Album> albums = albumService.searchActiveAlbumsByTitle(title);
        List<AlbumDto> albumDtos = albums.stream()
                .map(AlbumDto::fromEntitySimple)
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
                .map(AlbumDto::fromEntitySimple)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", albumDtos));
    }

    @PostMapping
    public ResponseEntity<?> createAlbum(@RequestBody Map<String, Object> payload) {
        // Only admins can create albums
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can create albums"));
        }

        // Validate required fields
        if (!payload.containsKey("title") || payload.get("title").toString().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Album title is required"));
        }

        String title = payload.get("title").toString();
        
        // Parse release date
        LocalDate releaseDate = null;
        if (payload.containsKey("releaseDate") && payload.get("releaseDate") != null) {
            try {
                releaseDate = LocalDate.parse(payload.get("releaseDate").toString());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid release date format. Use ISO date format (YYYY-MM-DD)"));
            }
        }
        
        String photo = payload.containsKey("photo") ? payload.get("photo").toString() : null;
        
        // Parse artist IDs
        List<UUID> artistIds = new ArrayList<>();
        if (payload.containsKey("artistIds") && payload.get("artistIds") instanceof List) {
            try {
                artistIds = ((List<?>) payload.get("artistIds"))
                        .stream()
                        .map(id -> UUID.fromString(id.toString()))
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
            }
        }

        try {
            Album createdAlbum = albumService.createAlbum(title, releaseDate, photo, artistIds);
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
    public ResponseEntity<?> updateAlbum(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        // Only admins can update albums
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can update albums"));
        }

        UUID albumId;
        try {
            albumId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid album ID format"));
        }

        String title = payload.containsKey("title") ? payload.get("title").toString() : null;
        
        // Parse release date
        LocalDate releaseDate = null;
        if (payload.containsKey("releaseDate") && payload.get("releaseDate") != null) {
            try {
                releaseDate = LocalDate.parse(payload.get("releaseDate").toString());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid release date format. Use ISO date format (YYYY-MM-DD)"));
            }
        }
        
        String photo = payload.containsKey("photo") ? payload.get("photo").toString() : null;

        try {
            Optional<Album> updatedAlbumOpt = albumService.updateAlbum(albumId, title, releaseDate, photo);
            
            // If album updated successfully and artists are provided, update artists
            if (updatedAlbumOpt.isPresent() && payload.containsKey("artistIds") && payload.get("artistIds") instanceof List) {
                try {
                    List<UUID> artistIds = ((List<?>) payload.get("artistIds"))
                            .stream()
                            .map(aId -> UUID.fromString(aId.toString()))
                            .collect(Collectors.toList());
                    
                    updatedAlbumOpt = albumService.setArtistsForAlbum(albumId, artistIds);
                } catch (Exception e) {
                    // Log but continue with the album update
                    System.err.println("Failed to update artists: " + e.getMessage());
                }
            }
            
            return updatedAlbumOpt
                    .map(album -> {
                        AlbumDto dto = AlbumDto.fromEntity(album);
                        return ResponseEntity.ok(dto);
                    })
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update album: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/artists")
    public ResponseEntity<?> addArtistsToAlbum(@PathVariable String id, @RequestBody Map<String, List<String>> payload) {
        // Only admins can modify album-artist relationships
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can modify album-artist relationships"));
        }

        UUID albumId;
        try {
            albumId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid album ID format"));
        }

        // Check if artist IDs are provided
        if (!payload.containsKey("artistIds") || payload.get("artistIds").isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Artist IDs are required"));
        }

        // Convert string IDs to UUIDs
        List<UUID> artistIds;
        try {
            artistIds = payload.get("artistIds").stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
        }

        return albumService.addArtistsToAlbum(albumId, artistIds)
                .map(album -> {
                    AlbumDto dto = AlbumDto.fromEntity(album);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/artists")
    public ResponseEntity<?> removeArtistsFromAlbum(@PathVariable String id, @RequestBody Map<String, List<String>> payload) {
        // Only admins can modify album-artist relationships
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can modify album-artist relationships"));
        }

        UUID albumId;
        try {
            albumId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid album ID format"));
        }

        // Check if artist IDs are provided
        if (!payload.containsKey("artistIds") || payload.get("artistIds").isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Artist IDs are required"));
        }

        // Convert string IDs to UUIDs
        List<UUID> artistIds;
        try {
            artistIds = payload.get("artistIds").stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
        }

        return albumService.removeArtistsFromAlbum(albumId, artistIds)
                .map(album -> {
                    AlbumDto dto = AlbumDto.fromEntity(album);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}