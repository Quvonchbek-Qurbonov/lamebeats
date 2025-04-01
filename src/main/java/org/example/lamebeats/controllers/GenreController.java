package org.example.lamebeats.controllers;

import org.example.lamebeats.dto.GenreDto;
import org.example.lamebeats.models.Genre;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.services.GenreService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreService genreService;
    private final CurrentUser currentUser;

    @Autowired
    public GenreController(GenreService genreService, CurrentUser currentUser) {
        this.genreService = genreService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGenres(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {

        Map<String, Object> response = genreService.getAllActiveGenresPaginated(page, limit);

        // Convert entities to DTOs
        List<Genre> genres = (List<Genre>) response.get("data");
        List<GenreDto> genreDtos = genres.stream()
                .map(GenreDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", genreDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllGenresIncludingDeleted() {
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can access this endpoint"));
        }

        List<Genre> genres = genreService.getAllGenres();
        List<GenreDto> genreDtos = genres.stream()
                .map(GenreDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", genreDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable String id) {
        UUID genreId;
        try {
            genreId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid genre ID format"));
        }

        return genreService.getActiveGenreById(genreId)
                .map(genre -> {
                    GenreDto dto = GenreDto.fromEntity(genre);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<?> getGenresByArtist(@PathVariable String artistId) {
        UUID artistUUID;
        try {
            artistUUID = UUID.fromString(artistId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid artist ID format"));
        }

        List<Genre> genres = genreService.getActiveGenresByArtistId(artistUUID);
        List<GenreDto> genreDtos = genres.stream()
                .map(GenreDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("data", genreDtos));
    }


    @PostMapping
    public ResponseEntity<?> createGenre(@RequestBody Map<String, String> payload) {
        // Check admin permissions
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can create genres"));
        }

        // Validate required fields
        if (!payload.containsKey("title") || payload.get("title").trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Genre title is required"));
        }

        String title = payload.get("title");

        try {
            // Check if genre already exists
            if (genreService.existsByTitle(title)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Genre with this title already exists"));
            }

            Genre createdGenre = genreService.createGenre(title);
            GenreDto dto = GenreDto.fromEntity(createdGenre);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create genre: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/artist/{artistId}")
    public ResponseEntity<?> addGenreToArtist(@PathVariable String id, @PathVariable String artistId) {
        // Check admin permissions
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only Admins can modify genre-artist relationships"));
        }

        UUID genreId, artistUUID;
        try {
            genreId = UUID.fromString(id);
            artistUUID = UUID.fromString(artistId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
        }

        return genreService.addGenreToArtist(genreId, artistUUID)
                .map(artist -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.notFound().build());
    }
}