package org.example.lamebeats.controllers;

import org.example.lamebeats.dto.AlbumDto;
import org.example.lamebeats.dto.ArtistDto;
import org.example.lamebeats.dto.SongDto;
import org.example.lamebeats.models.Album;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search")
    public ResponseEntity<?> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "album,song,artist") String type,
            @RequestParam(defaultValue = "10") int limit) {

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Search query (q) is required.");
        }


        List<String> types = Arrays.stream(type.toLowerCase().split(","))
                .map(String::trim)
                .filter(t -> t.equals("song") || t.equals("album") || t.equals("artist"))
                .collect(Collectors.toList());

        if (types.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid type parameter. Use 'album', 'song', or 'artist'.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("album", new HashMap<>());
        response.put("songs", new HashMap<>());
        response.put("artist", new HashMap<>());

        if (types.contains("album")) {
            List<Album> albums = searchService.searchAlbums(q, limit);
            if (!albums.isEmpty()) {
                List<AlbumDto> albumDtos = albums.stream()
                        .map(AlbumDto::fromEntity)
                        .collect(Collectors.toList());
                response.put("album", albumDtos);
            }
        }

        if (types.contains("song")) {
            List<Song> songs = searchService.searchSongs(q, limit);
            if (!songs.isEmpty()) {
                List<SongDto> songDtos = songs.stream()
                        .map(SongDto::fromEntity)
                        .collect(Collectors.toList());
                response.put("songs", songDtos);
            }
        }

        if (types.contains("artist")) {
            List<Artist> artists = searchService.searchArtists(q, limit);
            if (!artists.isEmpty()) {
                List<ArtistDto> artistDtos = artists.stream()
                        .map(ArtistDto::fromEntity)
                        .collect(Collectors.toList());
                response.put("artist", artistDtos);
            }
        }

        return ResponseEntity.ok(response);
    }
}