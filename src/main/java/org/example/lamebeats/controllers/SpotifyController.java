package org.example.lamebeats.controllers;

import org.example.lamebeats.models.Album;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.services.AlbumService;
import org.example.lamebeats.services.ArtistService;
import org.example.lamebeats.services.SongService;
import org.example.lamebeats.services.SpotifyService;
import org.example.lamebeats.utils.CurrentUser;
import org.example.lamebeats.utils.SpotifySearch;
import org.example.lamebeats.utils.parsers.SpotifySearchParser;
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
    private final AlbumService albumService;
    private final SongService songService;
    private final ArtistService artistService;
    private final CurrentUser currentUser;

    @Autowired
    public SpotifyController(SpotifyService spotifyService, CurrentUser currentUser, AlbumService albumService,
                             SongService songService, ArtistService artistService) {
        this.spotifyService = spotifyService;
        this.albumService = albumService;
        this.songService = songService;
        this.artistService = artistService;
        this.currentUser = currentUser;
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshSpotifyToken() {
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("error", "Only Admins can access this endpoint"));
        }

        String accessToken = spotifyService.getAccessToken();

        if (accessToken == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to retrieve Spotify access token");
            return ResponseEntity.status(500).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<SpotifySearch.SearchResponse> searchSpotify(
            @RequestParam String query,
            @RequestParam(defaultValue = "false") boolean album,
            @RequestParam(defaultValue = "false") boolean artist,
            @RequestParam(defaultValue = "false") boolean track,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {

        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(403).body(
                    SpotifySearch.SearchResponse.error("Only Admins can access this endpoint"));
        }

        if (query.isEmpty()) {
            return ResponseEntity.status(400).body(
                    SpotifySearch.SearchResponse.error("Query parameter cannot be empty"));
        }

        // Build the search type parameters
        StringBuilder typeParams = new StringBuilder();
        if (track) typeParams.append("track,");
        if (album) typeParams.append("album,");
        if (artist) typeParams.append("artist,");

        // Remove trailing comma if exists
        String types = typeParams.toString();
        if (types.endsWith(",")) {
            types = types.substring(0, types.length() - 1);
        }

        // Prepare the query
        String formattedQuery = "?q=" + query;

        // Fetch search results
        SpotifySearchParser.SearchResult searchResult = spotifyService.search(formattedQuery, types, page, limit);

        // Create response object
        SpotifySearch.SearchResponseData data = new SpotifySearch.SearchResponseData();
        int maxTotal = 0;

        // Process tracks if available
        if (searchResult.hasTracks()) {
            data.setSongs(mapTracks(searchResult.getTracks().getItems()));
            maxTotal = Math.max(maxTotal, searchResult.getTracks().getTotal());
        }

        // Process albums if available
        if (searchResult.hasAlbums()) {
            data.setAlbums(mapAlbums(searchResult.getAlbums().getItems()));
            maxTotal = Math.max(maxTotal, searchResult.getAlbums().getTotal());
        }

        // Process artists if available
        if (searchResult.hasArtists()) {
            data.setArtists(mapArtists(searchResult.getArtists().getItems()));
            maxTotal = Math.max(maxTotal, searchResult.getArtists().getTotal());
        }

        // Process playlists if needed in the future

        // Calculate pagination
        int totalPages = (int) Math.ceil((double) maxTotal / limit);

        // Create the final response
        SpotifySearch.SearchResponse response = new SpotifySearch.SearchResponse();
        response.setData(data);
        response.setLimit(limit);
        response.setTotal(maxTotal);
        response.setPages(totalPages);
        response.setPage(page);

        return ResponseEntity.ok(response);
    }

    /**
     * Map Spotify tracks to SongDto objects
     */
    private List<SpotifySearch.SongDto> mapTracks(List<SpotifySearchParser.Track> tracks) {
        return tracks.stream()
                .filter(track -> track != null) // Filter out null tracks from the list
                .map(track -> {
                    SpotifySearch.SongDto songDto = new SpotifySearch.SongDto();

                    // Set basic song info
                    songDto.setSpotifyId(track.getId());
                    songDto.setTitle(track.getName());
                    songDto.setDuration(track.getDuration_ms() / 1000); // Convert ms to seconds

                    // Check if song exists in DB
                    Song existingSong = songService.findBySpotifyId(track.getId());
                    if (existingSong != null) {
                        songDto.setId(existingSong.getId().toString());
                    }

                    // Map album
                    if (track.getAlbum() != null) {
                        SpotifySearch.AlbumDto albumDto = mapSingleAlbum(track.getAlbum());
                        songDto.setAlbum(albumDto);
                    }

                    // Map artists
                    List<SpotifySearch.ArtistDto> artistDtos = track.getArtists().stream()
                            .filter(artist -> artist != null)
                            .map(this::mapSingleArtist)
                            .collect(Collectors.toList());

                    songDto.setArtists(artistDtos);

                    return songDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Map Spotify albums to AlbumDto objects
     */
    private List<SpotifySearch.AlbumDto> mapAlbums(List<SpotifySearchParser.Album> albums) {
        return albums.stream()
                .filter(album -> album != null) // Filter out null albums
                .map(this::mapSingleAlbum)
                .collect(Collectors.toList());
    }

    /**
     * Map single Spotify album to AlbumDto
     */
    private SpotifySearch.AlbumDto mapSingleAlbum(SpotifySearchParser.Album album) {
        SpotifySearch.AlbumDto albumDto = new SpotifySearch.AlbumDto();

        // Set basic album info
        albumDto.setSpotifyId(album.getId());
        albumDto.setName(album.getName());
        albumDto.setReleaseDate(album.getRelease_date());

        // Check if album exists in DB
        Album existingAlbum = albumService.findBySpotifyId(album.getId());
        if (existingAlbum != null) {
            albumDto.setId(existingAlbum.getId().toString());
        }

        // Extract image URLs
        List<String> images = album.getImages().stream()
                .filter(image -> image != null && image.getUrl() != null)
                .map(SpotifySearchParser.Image::getUrl)
                .collect(Collectors.toList());
        albumDto.setImages(images);

        // Map artists
        List<SpotifySearch.ArtistDto> artistDtos = album.getArtists().stream()
                .filter(artist -> artist != null)
                .map(this::mapSingleArtist)
                .collect(Collectors.toList());

        albumDto.setArtists(artistDtos);

        return albumDto;
    }

    /**
     * Map Spotify artists to ArtistDto objects
     */
    private List<SpotifySearch.ArtistDto> mapArtists(List<SpotifySearchParser.Artist> artists) {
        return artists.stream()
                .filter(artist -> artist != null) // Filter out null artists
                .map(this::mapSingleArtist)
                .collect(Collectors.toList());
    }

    /**
     * Map single Spotify artist to ArtistDto
     */
    private SpotifySearch.ArtistDto mapSingleArtist(SpotifySearchParser.Artist artist) {
        SpotifySearch.ArtistDto artistDto = new SpotifySearch.ArtistDto();

        // Set basic artist info
        artistDto.setSpotifyId(artist.getId());
        artistDto.setName(artist.getName());

        // Check if artist exists in DB
        Artist existingArtist = artistService.findBySpotifyId(artist.getId());
        if (existingArtist != null) {
            artistDto.setId(existingArtist.getId().toString());
        }

        // Extract genres if available
        if (artist.getGenres() != null) {
            artistDto.setGenres(artist.getGenres());
        }

        // Extract image URLs
        List<String> images = artist.getImages().stream()
                .filter(image -> image != null && image.getUrl() != null)
                .map(SpotifySearchParser.Image::getUrl)
                .collect(Collectors.toList());
        artistDto.setImages(images);

        return artistDto;
    }
}
