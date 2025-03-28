package org.example.lamebeats.controllers;

import org.example.lamebeats.models.Track;
import org.example.lamebeats.services.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    @Autowired
    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping
    public List<Track> getAllTracks() {
        return trackService.getAllTracks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Track> getTrackById(@PathVariable Long id) {
        return trackService.getTrackById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Track> createTrack(@RequestBody Track track) {
        Track savedTrack = trackService.saveTrack(track);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTrack);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Track> updateTrack(@PathVariable Long id, @RequestBody Track track) {
        return trackService.getTrackById(id)
                .map(existingTrack -> {
                    track.setId(id);
                    return ResponseEntity.ok(trackService.saveTrack(track));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        if (!trackService.getTrackById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        trackService.deleteTrack(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/artist")
    public List<Track> findByArtist(@RequestParam String name) {
        return trackService.findTracksByArtist(name);
    }

    @GetMapping("/title")
    public List<Track> findByTitle(@RequestParam String title) {
        return trackService.findTracksByTitle(title);
    }

    @GetMapping("/genre")
    public List<Track> findByGenre(@RequestParam String genre) {
        return trackService.findTracksByGenre(genre);
    }

    @GetMapping("/album")
    public List<Track> findByAlbum(@RequestParam String album) {
        return trackService.findTracksByAlbum(album);
    }

    @GetMapping("/top")
    public List<Track> findTopTracks(@RequestParam(defaultValue = "10") Integer minPlays) {
        return trackService.findTopTracks(minPlays);
    }

    @GetMapping("/recent")
    public List<Track> findRecentTracks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        return trackService.findRecentTracks(since);
    }

    @GetMapping("/recently-added")
    public List<Track> findRecentlyAddedTracks() {
        return trackService.findRecentlyAddedTracks();
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<Void> incrementPlayCount(@PathVariable Long id) {
        if (!trackService.getTrackById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        trackService.incrementPlayCount(id);
        return ResponseEntity.ok().build();
    }
}