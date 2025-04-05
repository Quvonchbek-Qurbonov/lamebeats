package org.example.lamebeats.utils.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpotifyTrackParser {
    // Track fields
    private String id;
    private String name;
    private String uri;
    private long durationMs;
    private int popularity;
    private boolean explicit;
    private String previewUrl;
    private int trackNumber;
    private int discNumber;
    private boolean isLocal;
    private boolean isPlayable;
    private String href;
    private String type;

    // External identifiers
    private String spotifyUrl;
    private String isrc;

    // Album fields
    private String albumId;
    private String albumName;
    private String albumType;
    private String releaseDate;
    private int totalTracks;
    private List<String> albumImageUrls;

    // Artist fields
    private List<String> artistIds;
    private List<String> artistNames;

    // Static ObjectMapper for JSON parsing
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Empty constructor
    public SpotifyTrackParser() {}

    /**
     * Static method to parse a Spotify track JSON response into a SpotifyTrack object
     *
     * @param json The JSON string from Spotify API
     * @return SpotifyTrack object populated with data from the JSON
     * @throws RuntimeException if parsing fails
     */
    public static SpotifyTrackParser parseTrack(String json) {
        try {
            SpotifyTrackParser track = new SpotifyTrackParser();
            JsonNode rootNode = objectMapper.readTree(json);

            // Parse track basics
            track.id = rootNode.path("id").asText();
            track.name = rootNode.path("name").asText();
            track.uri = rootNode.path("uri").asText();
            track.durationMs = rootNode.path("duration_ms").asLong();
            track.popularity = rootNode.path("popularity").asInt();
            track.explicit = rootNode.path("explicit").asBoolean();
            track.previewUrl = rootNode.path("preview_url").isNull() ? null : rootNode.path("preview_url").asText();
            track.trackNumber = rootNode.path("track_number").asInt();
            track.discNumber = rootNode.path("disc_number").asInt();
            track.isLocal = rootNode.path("is_local").asBoolean();
            track.isPlayable = rootNode.path("is_playable").asBoolean();
            track.href = rootNode.path("href").asText();
            track.type = rootNode.path("type").asText();

            // Parse external URLs and IDs
            track.spotifyUrl = rootNode.path("external_urls").path("spotify").asText();
            track.isrc = rootNode.path("external_ids").path("isrc").asText();

            // Parse artists
            track.artistIds = new ArrayList<>();
            track.artistNames = new ArrayList<>();
            JsonNode artistsNode = rootNode.path("artists");
            if (artistsNode.isArray()) {
                for (JsonNode artistNode : artistsNode) {
                    track.artistIds.add(artistNode.path("id").asText());
                    track.artistNames.add(artistNode.path("name").asText());
                }
            }

            // Parse album
            JsonNode albumNode = rootNode.path("album");
            track.albumId = albumNode.path("id").asText();
            track.albumName = albumNode.path("name").asText();
            track.albumType = albumNode.path("album_type").asText();
            track.releaseDate = albumNode.path("release_date").asText();
            track.totalTracks = albumNode.path("total_tracks").asInt();

            // Parse album images
            track.albumImageUrls = new ArrayList<>();
            JsonNode imagesNode = albumNode.path("images");
            if (imagesNode.isArray()) {
                for (JsonNode imageNode : imagesNode) {
                    track.albumImageUrls.add(imageNode.path("url").asText());
                }
            }

            return track;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Spotify track JSON", e);
        }
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public int getDiscNumber() {
        return discNumber;
    }

    public void setDiscNumber(int discNumber) {
        this.discNumber = discNumber;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public boolean isPlayable() {
        return isPlayable;
    }

    public void setPlayable(boolean playable) {
        isPlayable = playable;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumType() {
        return albumType;
    }

    public void setAlbumType(String albumType) {
        this.albumType = albumType;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }

    public List<String> getAlbumImageUrls() {
        return albumImageUrls;
    }

    public void setAlbumImageUrls(List<String> albumImageUrls) {
        this.albumImageUrls = albumImageUrls;
    }

    public List<String> getArtistIds() {
        return artistIds;
    }

    public void setArtistIds(List<String> artistIds) {
        this.artistIds = artistIds;
    }

    public List<String> getArtistNames() {
        return artistNames;
    }

    public void setArtistNames(List<String> artistNames) {
        this.artistNames = artistNames;
    }
}