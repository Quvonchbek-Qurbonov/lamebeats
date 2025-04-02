package org.example.lamebeats.controllers;

import org.example.lamebeats.dto.SongDto;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.services.SongService;
import org.example.lamebeats.services.SpotifyService;
import org.example.lamebeats.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {
    private final SpotifyService spotifyService;
    private final CurrentUser currentUser;

    @Autowired
    public SpotifyController(SpotifyService spotifyService, CurrentUser currentUser) {
        this.spotifyService = spotifyService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> testingthigns() {
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("error", "Only Admins can access this endpoint"));
        }

        String accessToken = spotifyService.getAccessToken();

        // Handle potential null token
        if (accessToken == null) {
            // Use HashMap instead of Map.of() to allow null values if needed
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to retrieve Spotify access token");
            return ResponseEntity.status(500).body(response);
        }

        // Use HashMap instead of Map.of() to be consistent
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        return ResponseEntity.ok(response);
    }
}
