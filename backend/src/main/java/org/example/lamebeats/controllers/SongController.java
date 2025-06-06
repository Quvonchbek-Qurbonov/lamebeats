package org.example.lamebeats.controllers;

import org.example.lamebeats.dto.SongDto;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.services.SongService;
import org.example.lamebeats.services.SongStreamingService;
import org.example.lamebeats.services.SpotifyService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    private final SongService songService;
    private final CurrentUser currentUser;
    private final SongStreamingService songStreamingService;
    private final SpotifyService spotifyService;

    @Autowired
    public SongController(SongService songService, CurrentUser currentUser, SongStreamingService songStreamingService, SpotifyService spotifyService) {
        this.songService = songService;
        this.currentUser = currentUser;
        this.songStreamingService = songStreamingService;
        this.spotifyService = spotifyService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSongs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String albumId,
            @RequestParam(required = false) String artistId) {

        UUID albumUUID = null;
        UUID artistUUID = null;

        if (albumId != null && !albumId.isEmpty()) {
            try {
                albumUUID = UUID.fromString(albumId);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid album ID format"));
            }
        }

        if (artistId != null && !artistId.isEmpty()) {
            try {
                artistUUID = UUID.fromString(artistId);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
            }
        }

        Map<String, Object> response = songService.getAllActiveSongsPaginated(page, limit, albumUUID, artistUUID);

        // Convert entities to DTOs
        List<Song> songs = (List<Song>) response.get("data");
        List<SongDto> songDtos = songs.stream()
                .map(SongDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", songDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSongsIncludingDeleted() {
        // Only admins can see deleted songs
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can access this endpoint"));
        }

        List<Song> songs = songService.getAllSongs();
        List<SongDto> songDtos = songs.stream()
                .map(SongDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", songDtos));
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

    @GetMapping("/{id}/stream")
    public ResponseEntity<Resource> streamSong(
            @PathVariable String id) {
        return songStreamingService.streamSong(id);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<?> getPreviewUrl(@PathVariable String id) {
        List<String> trackPreviewUrls = spotifyService.getTrackPreviewUrls(id);

        return ResponseEntity.ok(Map.of("url", trackPreviewUrls.getFirst()));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSongs(@RequestParam String title) {
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Search title is required"));
        }

        List<Song> songs = songService.searchSongsByTitleOrArtist(title);
        List<SongDto> songDtos = songs.stream()
                .map(SongDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", songDtos));
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<?> getSongsByAlbum(@PathVariable String albumId) {
        UUID albumUUID;
        try {
            albumUUID = UUID.fromString(albumId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid album ID format"));
        }

        List<Song> songs = songService.getActiveSongsByAlbumId(albumUUID);
        List<SongDto> songDtos = songs.stream()
                .map(SongDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", songDtos));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<?> getSongsByArtist(@PathVariable String artistId) {
        UUID artistUUID;
        try {
            artistUUID = UUID.fromString(artistId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
        }

        List<Song> songs = songService.getActiveSongsByArtistId(artistUUID);
        List<SongDto> songDtos = songs.stream()
                .map(SongDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", songDtos));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<?> getSongsByGenre(@PathVariable String genreId) {
        UUID genreUUID;
        try {
            genreUUID = UUID.fromString(genreId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid genre ID format"));
        }

        List<Song> songs = songService.getSongsByGenreId(genreUUID);
        List<SongDto> songDtos = songs.stream()
                .map(SongDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", songDtos));
    }

    @GetMapping("/genres")
    public ResponseEntity<?> getSongsByGenres(
            @RequestParam List<String> genreIds,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {

        Set<UUID> genreUUIDs = new HashSet<>();
        try {
            genreUUIDs = genreIds.stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid genre ID format"));
        }

        if (genreUUIDs.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "At least one genre ID is required"));
        }

        Map<String, Object> response = songService.getSongsByGenreIdsPaginated(genreUUIDs, page, limit);

        // Convert entities to DTOs
        List<Song> songs = (List<Song>) response.get("data");
        List<SongDto> songDtos = songs.stream()
                .map(SongDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", songDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @PostMapping
    public ResponseEntity<?> createSong(@RequestBody Map<String, Object> payload) {
        // Only admins can create songs
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can create songs"));
        }

        // Validate required fields
        if (!payload.containsKey("title") || payload.get("title").toString().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Song title is required"));
        }

        String title = payload.get("title").toString();

        // Parse other fields
        Integer duration = null;
        if (payload.containsKey("duration") && payload.get("duration") != null) {
            try {
                duration = Integer.parseInt(payload.get("duration").toString());
                if (duration < 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Duration must be positive"));
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid duration format"));
            }
        }

        String fileUrl = payload.containsKey("fileUrl") ? payload.get("fileUrl").toString() : null;
        String spotifyId = payload.containsKey("spotifyId") ? payload.get("spotifyId").toString() : null;

        // Parse album ID
        UUID albumId = null;
        if (payload.containsKey("albumId") && payload.get("albumId") != null) {
            try {
                albumId = UUID.fromString(payload.get("albumId").toString());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid album ID format"));
            }
        }

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
            Song createdSong = songService.createSong(title, duration, fileUrl, albumId, artistIds, spotifyId);
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
    public ResponseEntity<?> updateSong(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        // Only admins can update songs
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can update songs"));
        }

        UUID songId;
        try {
            songId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        String title = payload.containsKey("title") ? payload.get("title").toString() : null;

        // Parse duration
        Integer duration = null;
        if (payload.containsKey("duration") && payload.get("duration") != null) {
            try {
                duration = Integer.parseInt(payload.get("duration").toString());
                if (duration < 0) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Duration must be positive"));
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid duration format"));
            }
        }

        String genre = payload.containsKey("genre") ? payload.get("genre").toString() : null;
        String fileUrl = payload.containsKey("fileUrl") ? payload.get("fileUrl").toString() : null;

        // Parse album ID
        UUID albumId = null;
        if (payload.containsKey("albumId")) {
            if (payload.get("albumId") != null && !payload.get("albumId").toString().trim().isEmpty()) {
                try {
                    albumId = UUID.fromString(payload.get("albumId").toString());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid album ID format"));
                }
            }
            // If albumId is explicitly null or empty, we'll remove the album association
        }

        try {
            Optional<Song> updatedSongOpt = songService.updateSong(songId, title, duration, genre, fileUrl, albumId);

            // If song updated successfully and artists are provided, update artists
            if (updatedSongOpt.isPresent() && payload.containsKey("artistIds") && payload.get("artistIds") instanceof List) {
                try {
                    List<UUID> artistIds = ((List<?>) payload.get("artistIds"))
                            .stream()
                            .map(aId -> UUID.fromString(aId.toString()))
                            .collect(Collectors.toList());

                    updatedSongOpt = songService.setArtistsForSong(songId, artistIds);
                } catch (Exception e) {
                    // Log but continue with the song update
                    System.err.println("Failed to update artists: " + e.getMessage());
                }
            }

            return updatedSongOpt
                    .map(song -> {
                        SongDto dto = SongDto.fromEntity(song);
                        return ResponseEntity.ok(dto);
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update song: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/artists")
    public ResponseEntity<?> addArtistsToSong(@PathVariable String id, @RequestBody Map<String, List<String>> payload) {
        // Only admins can modify song-artist relationships
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can modify song-artist relationships"));
        }

        UUID songId;
        try {
            songId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
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

        return songService.addArtistsToSong(songId, artistIds)
                .map(song -> {
                    SongDto dto = SongDto.fromEntity(song);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/artists")
    public ResponseEntity<?> removeArtistsFromSong(@PathVariable String id, @RequestBody Map<String, List<String>> payload) {
        // Only admins can modify song-artist relationships
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can modify song-artist relationships"));
        }

        UUID songId;
        try {
            songId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
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

        return songService.removeArtistsFromSong(songId, artistIds)
                .map(song -> {
                    SongDto dto = SongDto.fromEntity(song);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable String id, @RequestParam(defaultValue = "false") boolean hard) {
        // Only admins can delete songs
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only Admins can delete songs"));
        }

        UUID songId;
        try {
            songId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        boolean deleted;
        if (hard) {
            deleted = songService.hardDeleteSong(songId);
        } else {
            deleted = songService.softDeleteSong(songId);
        }

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

}