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
import org.example.lamebeats.utils.parsers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
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

    @PostMapping("add-track/{spotifyId}")
    public ResponseEntity<Map<String, Object>> addTrack(@PathVariable String spotifyId) {
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("error", "Only Admins can access this endpoint"));
        }

        if (spotifyId.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "ID parameter cannot be empty"));
        }

        Song existingSong = songService.findBySpotifyId(spotifyId);
        if (existingSong != null) {
            return ResponseEntity.status(409).body(Map.of("error", "Song already exists with id: " + existingSong.getId()));
        }

        // Fetch track details from Spotify
        SpotifyTrackParser trackSpotify = spotifyService.getTrackByIdFromSpotify(spotifyId);

        Album album = albumService.findBySpotifyId(trackSpotify.getAlbumId());
        if (album == null) {
            SpotifyAlbumParser spotifyAlbum = spotifyService.getAlbumByIdFromSpotify(trackSpotify.getAlbumId());

            // checking for album artists
            List<UUID> artistIds = new ArrayList<>();
            for (String spotifyArtistId : trackSpotify.getArtistIds()) {
                Artist artist = artistService.findBySpotifyId(spotifyArtistId);
                if (artist == null) {
                    // Retrieve and create new artist if not found
                    SpotifyArtistParser artistSpotify = spotifyService.getArtistByIdFromSpotify(spotifyArtistId);

                    String imageUrl = artistSpotify.getImages().isEmpty() ? null : artistSpotify.getImages().get(0).get("url").toString();

                    artist = artistService.createArtist(artistSpotify.getName(), imageUrl, spotifyArtistId);
                }
                artistIds.add(artist.getId());
            }

            String imageUrl = spotifyAlbum.getImages().isEmpty() ? null : spotifyAlbum.getImages().get(0).get("url").toString();

            album = albumService.createAlbum(spotifyAlbum.getName(), LocalDate.parse(spotifyAlbum.getReleaseDate()),
                    imageUrl, artistIds,
                    spotifyAlbum.getId());
        }

        List<String> trackPreviewUrls = spotifyService.getTrackPreviewUrls(spotifyId);

        //TODO adding genre(s) to artist
        Song newSong = songService.createSong(trackSpotify.getName(), (int) (trackSpotify.getDurationMs() / 1000), trackPreviewUrls.getFirst().toString(), album.getId(), album.getArtistIds(), trackSpotify.getId());

        return ResponseEntity.status(200).body(Map.of("data", newSong));
    }

    @PostMapping("add-album/{spotifyId}")
    public ResponseEntity<Map<String, Object>> addAllAlbumTracks(@PathVariable String spotifyId) {
        if (!CurrentUser.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("error", "Only Admins can access this endpoint"));
        }

        if (spotifyId.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "ID parameter cannot be empty"));
        }

        SpotifyAlbumParser albumSpotify = spotifyService.getAlbumByIdFromSpotify(spotifyId);

        SpotifyAlbumTracks albumTracks = spotifyService.getAlbumTracks(spotifyId);
        if (albumTracks == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Album tracks not found"));
        }

        // Check if the album already exists in the database
        Album existingAlbum = albumService.findBySpotifyId(spotifyId);
        if (existingAlbum == null) {
            List<UUID> artistIds = new ArrayList<>();
            for (String artistId : albumSpotify.getArtistIds()) {
                Artist artist = artistService.findBySpotifyId(artistId);
                if (artist == null) {
                    // Retrieve and create new artist if not found
                    SpotifyArtistParser artistSpotify = spotifyService.getArtistByIdFromSpotify(artistId);

                    String imageUrl = artistSpotify.getImages().isEmpty() ? null : artistSpotify.getImages().get(0).get("url").toString();

                    artist = artistService.createArtist(artistSpotify.getName(), imageUrl, artistId);
                }
                artistIds.add(artist.getId());
            }

            Album newAlbum = albumService.createAlbum(albumSpotify.getName(), LocalDate.parse(albumSpotify.getReleaseDate()),
                    albumSpotify.getImages().isEmpty() ? null : albumSpotify.getImages().get(0).get("url").toString(),
                    artistIds, spotifyId);
            existingAlbum = newAlbum;
        }

        // Only add songs that don't exist yet
        List<Song> newSongs = new ArrayList<>();
        List<Song> allSongs = new ArrayList<>();

        for (SpotifyAlbumTracks.TrackItem singleSong : albumTracks.getTracks()) {
            Song existingSong = songService.findBySpotifyId(singleSong.getId());

            if (existingSong == null) {
                Song newSong = new Song();
                newSong.setId(UUID.randomUUID());
                newSong.setTitle(singleSong.getName());
                newSong.setDuration((int) (singleSong.getDurationMs() / 1000));
                newSong.setSpotifyId(singleSong.getId());
                newSong.setAlbum(existingAlbum);

                List<String> trackPreviewUrls = spotifyService.getTrackPreviewUrls(singleSong.getId());
                newSong.setFileUrl(trackPreviewUrls.getFirst().toString());

                Set<Artist> artists = new HashSet<>();
                for (SpotifyAlbumTracks.ArtistItem artist : singleSong.getArtists()) {
                    Artist existingArtist = artistService.findBySpotifyId(artist.getId());
                    if (existingArtist == null) {
                        // Retrieve and create new artist if not found
                        SpotifyArtistParser artistSpotify = spotifyService.getArtistByIdFromSpotify(artist.getId());

                        String imageUrl = artistSpotify.getImages().isEmpty() ? null : artistSpotify.getImages().get(0).get("url").toString();

                        existingArtist = artistService.createArtist(artistSpotify.getName(), imageUrl, artistSpotify.getId());
                    }
                    artists.add(existingArtist);
                }
                newSong.setArtists(artists);

                // Only add new songs to the list for bulk creation
                newSongs.add(newSong);
            }

            // Keep track of all songs for the response
            allSongs.add(existingSong != null ? existingSong : newSongs.get(newSongs.size() - 1));
        }

        // Only create songs that don't exist yet
        List<Song> createdSongs = newSongs.isEmpty() ?
                new ArrayList<>() :
                songService.createBulkSongs(newSongs);

        // Return all songs (both existing and newly created)
        return ResponseEntity.status(200).body(Map.of(
                "songsCount", allSongs.size(),
                "newSongsCount", createdSongs.size(),
                "songs", allSongs
        ));
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
