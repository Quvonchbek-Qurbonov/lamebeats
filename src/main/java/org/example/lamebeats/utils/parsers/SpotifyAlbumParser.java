package org.example.lamebeats.utils.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpotifyAlbumParser {
    // Album fields
    private String id;
    private String name;
    private String albumType;
    private int totalTracks;
    private boolean isPlayable;
    private String href;
    private String uri;
    private String type;
    private String releaseDate;
    private String releaseDatePrecision;
    private String label;
    private int popularity;
    
    // External URLs and IDs
    private String spotifyUrl;
    private String upc;
    
    // Images
    private List<Map<String, Object>> images;
    
    // Artists
    private List<Map<String, Object>> artists;
    
    // Tracks
    private List<Map<String, Object>> tracks;
    private int tracksTotal;
    private String tracksHref;
    
    // Copyrights
    private List<Map<String, String>> copyrights;
    
    // Genres
    private List<String> genres;
    
    // Static ObjectMapper for JSON parsing
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // Empty constructor
    public SpotifyAlbumParser() {}
    
    /**
     * Static method to parse a Spotify album JSON response into a SpotifyAlbum object
     * 
     * @param json The JSON string from Spotify API
     * @return SpotifyAlbum object populated with data from the JSON
     * @throws RuntimeException if parsing fails
     */
    public static SpotifyAlbumParser parseAlbum(String json) {
        try {
            SpotifyAlbumParser album = new SpotifyAlbumParser();
            JsonNode rootNode = objectMapper.readTree(json);
            
            // Parse album basics
            album.id = rootNode.path("id").asText();
            album.name = rootNode.path("name").asText();
            album.albumType = rootNode.path("album_type").asText();
            album.totalTracks = rootNode.path("total_tracks").asInt();
            album.isPlayable = rootNode.path("is_playable").asBoolean();
            album.href = rootNode.path("href").asText();
            album.uri = rootNode.path("uri").asText();
            album.type = rootNode.path("type").asText();
            album.releaseDate = rootNode.path("release_date").asText();
            album.releaseDatePrecision = rootNode.path("release_date_precision").asText();
            album.label = rootNode.path("label").asText();
            album.popularity = rootNode.path("popularity").asInt();
            
            // External URLs and IDs
            album.spotifyUrl = rootNode.path("external_urls").path("spotify").asText();
            album.upc = rootNode.path("external_ids").path("upc").asText();
            
            // Parse images
            album.images = new ArrayList<>();
            JsonNode imagesNode = rootNode.path("images");
            if (imagesNode.isArray()) {
                for (JsonNode imageNode : imagesNode) {
                    Map<String, Object> image = new HashMap<>();
                    image.put("url", imageNode.path("url").asText());
                    image.put("height", imageNode.path("height").asInt());
                    image.put("width", imageNode.path("width").asInt());
                    album.images.add(image);
                }
            }
            
            // Parse artists
            album.artists = new ArrayList<>();
            JsonNode artistsNode = rootNode.path("artists");
            if (artistsNode.isArray()) {
                for (JsonNode artistNode : artistsNode) {
                    Map<String, Object> artist = new HashMap<>();
                    artist.put("id", artistNode.path("id").asText());
                    artist.put("name", artistNode.path("name").asText());
                    artist.put("uri", artistNode.path("uri").asText());
                    artist.put("href", artistNode.path("href").asText());
                    artist.put("type", artistNode.path("type").asText());
                    
                    Map<String, String> artistExternalUrls = new HashMap<>();
                    artistExternalUrls.put("spotify", artistNode.path("external_urls").path("spotify").asText());
                    artist.put("external_urls", artistExternalUrls);
                    
                    album.artists.add(artist);
                }
            }
            
            // Parse tracks
            album.tracks = new ArrayList<>();
            JsonNode tracksNode = rootNode.path("tracks");
            album.tracksHref = tracksNode.path("href").asText();
            album.tracksTotal = tracksNode.path("total").asInt();
            
            JsonNode tracksItemsNode = tracksNode.path("items");
            if (tracksItemsNode.isArray()) {
                for (JsonNode trackNode : tracksItemsNode) {
                    Map<String, Object> track = new HashMap<>();
                    track.put("id", trackNode.path("id").asText());
                    track.put("name", trackNode.path("name").asText());
                    track.put("uri", trackNode.path("uri").asText());
                    track.put("href", trackNode.path("href").asText());
                    track.put("type", trackNode.path("type").asText());
                    track.put("duration_ms", trackNode.path("duration_ms").asLong());
                    track.put("disc_number", trackNode.path("disc_number").asInt());
                    track.put("track_number", trackNode.path("track_number").asInt());
                    track.put("explicit", trackNode.path("explicit").asBoolean());
                    track.put("is_playable", trackNode.path("is_playable").asBoolean());
                    track.put("is_local", trackNode.path("is_local").asBoolean());
                    track.put("preview_url", trackNode.path("preview_url").isNull() ? 
                                null : trackNode.path("preview_url").asText());
                    
                    Map<String, String> trackExternalUrls = new HashMap<>();
                    trackExternalUrls.put("spotify", trackNode.path("external_urls").path("spotify").asText());
                    track.put("external_urls", trackExternalUrls);
                    
                    // Track artists
                    List<Map<String, Object>> trackArtists = new ArrayList<>();
                    JsonNode trackArtistsNode = trackNode.path("artists");
                    if (trackArtistsNode.isArray()) {
                        for (JsonNode artistNode : trackArtistsNode) {
                            Map<String, Object> artist = new HashMap<>();
                            artist.put("id", artistNode.path("id").asText());
                            artist.put("name", artistNode.path("name").asText());
                            artist.put("uri", artistNode.path("uri").asText());
                            artist.put("href", artistNode.path("href").asText());
                            artist.put("type", artistNode.path("type").asText());
                            
                            Map<String, String> artistExternalUrls = new HashMap<>();
                            artistExternalUrls.put("spotify", artistNode.path("external_urls").path("spotify").asText());
                            artist.put("external_urls", artistExternalUrls);
                            
                            trackArtists.add(artist);
                        }
                    }
                    track.put("artists", trackArtists);
                    
                    album.tracks.add(track);
                }
            }
            
            // Parse copyrights
            album.copyrights = new ArrayList<>();
            JsonNode copyrightsNode = rootNode.path("copyrights");
            if (copyrightsNode.isArray()) {
                for (JsonNode copyrightNode : copyrightsNode) {
                    Map<String, String> copyright = new HashMap<>();
                    copyright.put("text", copyrightNode.path("text").asText());
                    copyright.put("type", copyrightNode.path("type").asText());
                    album.copyrights.add(copyright);
                }
            }
            
            // Parse genres
            album.genres = new ArrayList<>();
            JsonNode genresNode = rootNode.path("genres");
            if (genresNode.isArray()) {
                for (JsonNode genreNode : genresNode) {
                    album.genres.add(genreNode.asText());
                }
            }
            
            return album;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Spotify album JSON", e);
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

    public String getAlbumType() {
        return albumType;
    }

    public void setAlbumType(String albumType) {
        this.albumType = albumType;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDatePrecision() {
        return releaseDatePrecision;
    }

    public void setReleaseDatePrecision(String releaseDatePrecision) {
        this.releaseDatePrecision = releaseDatePrecision;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public List<Map<String, Object>> getImages() {
        return images;
    }

    public void setImages(List<Map<String, Object>> images) {
        this.images = images;
    }

    public List<Map<String, Object>> getArtists() {
        return artists;
    }

    public void setArtists(List<Map<String, Object>> artists) {
        this.artists = artists;
    }

    public List<Map<String, Object>> getTracks() {
        return tracks;
    }

    public void setTracks(List<Map<String, Object>> tracks) {
        this.tracks = tracks;
    }

    public int getTracksTotal() {
        return tracksTotal;
    }

    public void setTracksTotal(int tracksTotal) {
        this.tracksTotal = tracksTotal;
    }

    public String getTracksHref() {
        return tracksHref;
    }

    public void setTracksHref(String tracksHref) {
        this.tracksHref = tracksHref;
    }

    public List<Map<String, String>> getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(List<Map<String, String>> copyrights) {
        this.copyrights = copyrights;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
    
    // Convenience methods
    
    /**
     * Get album cover image URL (largest available)
     */
    public String getCoverImageUrl() {
        if (images != null && !images.isEmpty()) {
            return (String) images.get(0).get("url");
        }
        return null;
    }
    
    /**
     * Get thumbnail image URL (smallest available)
     */
    public String getThumbnailUrl() {
        if (images != null && !images.isEmpty()) {
            return (String) images.get(images.size() - 1).get("url");
        }
        return null;
    }
    
    /**
     * Get primary artist name
     */
    public String getMainArtistName() {
        if (artists != null && !artists.isEmpty()) {
            return (String) artists.get(0).get("name");
        }
        return null;
    }
    
    /**
     * Get all artist names as a comma-separated string
     */
    public String getAllArtistNames() {
        if (artists == null || artists.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < artists.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(artists.get(i).get("name"));
        }
        return sb.toString();
    }
}