package org.example.lamebeats.controllers;

import org.example.lamebeats.dto.ArtistDto;
import org.example.lamebeats.dto.ArtistGenreDto;
import org.example.lamebeats.dto.GenreDto;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.ArtistGenre;
import org.example.lamebeats.models.Genre;
import org.example.lamebeats.services.ArtistService;
import org.example.lamebeats.services.GenreService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {
    private final ArtistService artistService;
    private final GenreService genreService;
    private final CurrentUser currentUser;

    @Autowired
    public ArtistController(ArtistService artistService, GenreService genreService, CurrentUser currentUser) {
        this.artistService = artistService;
        this.genreService = genreService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllArtists(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {

        Map<String, Object> response = artistService.getAllActiveArtistsPaginated(page, limit);

        // Convert entities to DTOs
        List<Artist> artists = (List<Artist>) response.get("data");
        List<ArtistDto> artistDtos = artists.stream()
                .map(ArtistDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", artistDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllArtistsIncludingDeleted() {
        // Only admins can see deleted artists
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can access this endpoint"));
        }

        List<Artist> artists = artistService.getAllArtists();
        List<ArtistDto> artistDtos = artists.stream()
                .map(ArtistDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", artistDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArtistById(@PathVariable String id) {
        UUID artistId;
        try {
            artistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
        }

        return artistService.getActiveArtistById(artistId)
                .map(artist -> {
                    ArtistDto dto = ArtistDto.fromEntity(artist);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchArtists(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Search term is required"));
        }

        List<Artist> artists = artistService.searchActiveArtistsByName(name);
        List<ArtistDto> artistDtos = artists.stream()
                .map(ArtistDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", artistDtos));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<?> getArtistsByGenre(@PathVariable String genreId) {
        UUID genreUUID;
        try {
            genreUUID = UUID.fromString(genreId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid genre ID format"));
        }

        List<Artist> artists = artistService.getActiveArtistsByGenreId(genreUUID);
        List<ArtistDto> artistDtos = artists.stream()
                .map(ArtistDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", artistDtos));
    }

    @PostMapping
    public ResponseEntity<?> createArtist(@RequestBody Map<String, Object> payload) {
        // Only admins can create artists
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can create artists"));
        }

        // Validate required fields
        if (!payload.containsKey("name") || payload.get("name").toString().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Artist name is required"));
        }

        String name = payload.get("name").toString();
        String photo = payload.containsKey("photo") ? payload.get("photo").toString() : null;
        String spotifyId = payload.containsKey("spotifyId") ? payload.get("spotifyId").toString() : null;

        try {
            // Check if artist already exists
            if (artistService.existsByName(name)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Artist with this name already exists"));
            }

            Artist createdArtist = artistService.createArtist(name, photo, spotifyId);

            // Handle genre IDs if provided
            if (payload.containsKey("genreIds") && payload.get("genreIds") instanceof List) {
                try {
                    List<UUID> genreIds = ((List<?>) payload.get("genreIds"))
                            .stream()
                            .map(id -> UUID.fromString(id.toString()))
                            .collect(Collectors.toList());

                    artistService.addGenresToArtist(createdArtist.getId(), genreIds);

                    // Reload artist to include genres
                    createdArtist = artistService.getArtistById(createdArtist.getId()).orElse(createdArtist);
                } catch (Exception e) {
                    // Log but don't fail if genre association fails
                    System.err.println("Failed to associate genres: " + e.getMessage());
                }
            }

            ArtistDto dto = ArtistDto.fromEntity(createdArtist);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create artist: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArtist(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        // Only admins can update artists
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can update artists"));
        }

        UUID artistId;
        try {
            artistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
        }

        String name = payload.containsKey("name") ? payload.get("name").toString() : null;
        String photo = payload.containsKey("photo") ? payload.get("photo").toString() : null;

        try {
            Optional<Artist> updatedArtistOpt = artistService.updateArtist(artistId, name, photo);

            // If artist updated successfully and genres are provided, update genres
            if (updatedArtistOpt.isPresent() && payload.containsKey("genreIds") && payload.get("genreIds") instanceof List) {
                try {
                    List<UUID> genreIds = ((List<?>) payload.get("genreIds"))
                            .stream()
                            .map(gId -> UUID.fromString(gId.toString()))
                            .collect(Collectors.toList());

                    updatedArtistOpt = artistService.setGenresForArtist(artistId, genreIds);
                } catch (Exception e) {
                    // Log but continue with the artist update
                    System.err.println("Failed to update genres: " + e.getMessage());
                }
            }

            return updatedArtistOpt
                    .map(artist -> {
                        ArtistDto dto = ArtistDto.fromEntity(artist);
                        return ResponseEntity.ok(dto);
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update artist: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArtist(@PathVariable String id, @RequestParam(defaultValue = "false") boolean hard) {
        // Only admins can delete artists
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can delete artists"));
        }

        UUID artistId;
        try {
            artistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
        }

        boolean deleted;
        if (hard) {
            deleted = artistService.hardDeleteArtist(artistId);
        } else {
            deleted = artistService.softDeleteArtist(artistId);
        }

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/genres")
    public ResponseEntity<?> addGenresToArtist(@PathVariable String id, @RequestBody Map<String, List<String>> payload) {
        // Only admins can modify artist-genre relationships
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can modify artist-genre relationships"));
        }

        UUID artistId;
        try {
            artistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
        }

        // Check if genre IDs are provided
        if (!payload.containsKey("genreIds") || payload.get("genreIds").isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Genre IDs are required"));
        }

        // Convert string IDs to UUIDs
        List<UUID> genreIds;
        try {
            genreIds = payload.get("genreIds").stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid genre ID format"));
        }

        return artistService.addGenresToArtist(artistId, genreIds)
                .map(artist -> {
                    ArtistDto dto = ArtistDto.fromEntity(artist);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/genres")
    public ResponseEntity<?> getGenresForArtist(@PathVariable String id) {
        UUID artistId;
        try {
            artistId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
        }

        return artistService.getActiveArtistById(artistId)
                .map(artist -> {
                    List<GenreDto> genreDtos = artist.getGenres().stream()
                            .map(GenreDto::fromEntity)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(Map.of("data", genreDtos));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}