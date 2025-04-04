package org.example.lamebeats.utils.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpotifyArtistParser {
    // Artist basics
    private String id;
    private String name;
    private String uri;
    private String href;
    private String type;
    private int popularity;
    
    // External URLs
    private String spotifyUrl;
    
    // Followers
    private Integer followerCount;
    
    // Images
    private List<Map<String, Object>> images;
    
    // Genres
    private List<String> genres;
    
    // Static ObjectMapper for JSON parsing
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // Empty constructor
    public SpotifyArtistParser() {}
    
    /**
     * Static method to parse a Spotify artist JSON response into a SpotifyArtist object
     * 
     * @param json The JSON string from Spotify API
     * @return SpotifyArtist object populated with data from the JSON
     * @throws RuntimeException if parsing fails
     */
    public static SpotifyArtistParser parseArtist(String json) {
        try {
            SpotifyArtistParser artist = new SpotifyArtistParser();
            JsonNode rootNode = objectMapper.readTree(json);
            
            // Parse artist basics
            artist.id = rootNode.path("id").asText();
            artist.name = rootNode.path("name").asText();
            artist.uri = rootNode.path("uri").asText();
            artist.href = rootNode.path("href").asText();
            artist.type = rootNode.path("type").asText();
            artist.popularity = rootNode.path("popularity").asInt();
            
            // External URLs
            artist.spotifyUrl = rootNode.path("external_urls").path("spotify").asText();
            
            // Followers
            artist.followerCount = rootNode.path("followers").path("total").asInt();
            
            // Parse images
            artist.images = new ArrayList<>();
            JsonNode imagesNode = rootNode.path("images");
            if (imagesNode.isArray()) {
                for (JsonNode imageNode : imagesNode) {
                    Map<String, Object> image = new HashMap<>();
                    image.put("url", imageNode.path("url").asText());
                    image.put("height", imageNode.path("height").asInt());
                    image.put("width", imageNode.path("width").asInt());
                    artist.images.add(image);
                }
            }
            
            // Parse genres
            artist.genres = new ArrayList<>();
            JsonNode genresNode = rootNode.path("genres");
            if (genresNode.isArray()) {
                for (JsonNode genreNode : genresNode) {
                    artist.genres.add(genreNode.asText());
                }
            }
            
            return artist;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Spotify artist JSON", e);
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

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    public List<Map<String, Object>> getImages() {
        return images;
    }

    public void setImages(List<Map<String, Object>> images) {
        this.images = images;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}