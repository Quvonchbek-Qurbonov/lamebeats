package org.example.lamebeats.controllers;

import org.example.lamebeats.dto.RecentTrackDto;
import org.example.lamebeats.models.RecentTrack;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.services.RecentTrackService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recent-tracks")
public class RecentTrackController {
    private final RecentTrackService recentTrackService;
    private final CurrentUser currentUser;

    @Autowired
    public RecentTrackController(RecentTrackService recentTrackService, CurrentUser currentUser) {
        this.recentTrackService = recentTrackService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<?> getCurrentUserRecentTracks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {

        UUID userId = currentUser.getCurrentUserId();

        Map<String, Object> response = recentTrackService.getRecentTracksByUserPaginated(userId, page, limit);

        // Convert entities to DTOs
        List<RecentTrack> tracks = (List<RecentTrack>) response.get("data");
        List<RecentTrackDto> trackDtos = tracks.stream()
                .map(RecentTrackDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", trackDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRecentTracks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {

        // Only admins can view all recent tracks
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only Admins can access this endpoint"));
        }

        Map<String, Object> response = recentTrackService.getAllRecentTracksPaginated(page, limit);

        // Convert entities to DTOs
        List<RecentTrack> tracks = (List<RecentTrack>) response.get("data");
        List<RecentTrackDto> trackDtos = tracks.stream()
                .map(RecentTrackDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", trackDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRecentTracks(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {

        // Only admins can view other users' recent tracks
        if (!CurrentUser.isAdmin() && !userId.equals(currentUser.getCurrentUserId().toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only view your own recent tracks"));
        }

        UUID userUUID;
        try {
            userUUID = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid user ID format"));
        }

        Map<String, Object> response = recentTrackService.getRecentTracksByUserPaginated(userUUID, page, limit);

        // Convert entities to DTOssdaw
        List<RecentTrack> tracks = (List<RecentTrack>) response.get("data");
        List<RecentTrackDto> trackDtos = tracks.stream()
                .map(RecentTrackDto::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> dtoResponse = new HashMap<>(response);
        dtoResponse.put("data", trackDtos);

        return ResponseEntity.ok(dtoResponse);
    }

    @PostMapping("/record")
    public ResponseEntity<?> recordRecentTrack(@RequestBody Map<String, Object> payload) {
        // User must be authenticated
        UUID userId = currentUser.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        // Validate required fields
        if (!payload.containsKey("songId") || payload.get("songId") == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Song ID is required"));
        }

        UUID songId;
        try {
            songId = UUID.fromString(payload.get("songId").toString());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid song ID format"));
        }

        try {
            RecentTrack recentTrack = recentTrackService.recordRecentTrack(userId, songId);
            RecentTrackDto dto = RecentTrackDto.fromEntity(recentTrack);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to record track: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecentTrack(@PathVariable String id) {
        // Only admins can delete recent tracks
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only Admins can delete recent tracks"));
        }

        UUID trackId;
        try {
            trackId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid track ID format"));
        }

        boolean deleted = recentTrackService.deleteRecentTrack(trackId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Recent track deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteUserRecentTracks(@PathVariable String userId) {
        // Only admins can delete user history
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only Admins can delete user history"));
        }

        UUID userUUID;
        try {
            userUUID = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid user ID format"));
        }

        long count = recentTrackService.deleteRecentTracksByUser(userUUID);
        return ResponseEntity.ok(Map.of(
                "message", "Deleted " + count + " recent tracks for user",
                "count", count
        ));
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<?> cleanupOldTracks(
            @RequestParam(defaultValue = "30") int daysToKeep) {

        // Only admins can perform cleanup
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only Admins can perform history cleanup"));
        }

        // Calculate the cutoff date
        LocalDateTime cutoffDate = LocalDateTime.now().minus(daysToKeep, ChronoUnit.DAYS);
        long count = recentTrackService.cleanupOldTracks(cutoffDate);

        return ResponseEntity.ok(Map.of(
                "message", "Cleaned up " + count + " recent tracks older than " + daysToKeep + " days",
                "count", count,
                "cutoffDate", cutoffDate
        ));
    }
}