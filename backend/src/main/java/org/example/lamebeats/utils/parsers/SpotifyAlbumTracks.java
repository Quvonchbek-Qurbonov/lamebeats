package org.example.lamebeats.utils.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.lamebeats.models.Artist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SpotifyAlbumTracks {
    // Pagination info
    private String href;
    private int limit;
    private String next;
    private String previous;
    private int offset;
    private int total;
    
    // Track items
    private List<TrackItem> tracks;
    
    // Static ObjectMapper for JSON parsing
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Inner class representing a track in the album
     */
    public static class TrackItem {
        private String id;
        private String name;
        private String uri;
        private String href;
        private String type;
        private int discNumber;
        private int trackNumber;
        private long durationMs;
        private boolean explicit;
        private boolean isPlayable;
        private boolean isLocal;
        private String previewUrl;
        private String spotifyUrl;
        private List<ArtistItem> artists;
        
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

        public int getDiscNumber() {
            return discNumber;
        }

        public void setDiscNumber(int discNumber) {
            this.discNumber = discNumber;
        }

        public int getTrackNumber() {
            return trackNumber;
        }

        public void setTrackNumber(int trackNumber) {
            this.trackNumber = trackNumber;
        }

        public long getDurationMs() {
            return durationMs;
        }

        public void setDurationMs(long durationMs) {
            this.durationMs = durationMs;
        }

        public boolean isExplicit() {
            return explicit;
        }

        public void setExplicit(boolean explicit) {
            this.explicit = explicit;
        }

        public boolean isPlayable() {
            return isPlayable;
        }

        public void setPlayable(boolean playable) {
            isPlayable = playable;
        }

        public boolean isLocal() {
            return isLocal;
        }

        public void setLocal(boolean local) {
            isLocal = local;
        }

        public String getPreviewUrl() {
            return previewUrl;
        }

        public void setPreviewUrl(String previewUrl) {
            this.previewUrl = previewUrl;
        }

        public String getSpotifyUrl() {
            return spotifyUrl;
        }

        public void setSpotifyUrl(String spotifyUrl) {
            this.spotifyUrl = spotifyUrl;
        }

        public List<ArtistItem> getArtists() {
            return artists;
        }

        public void setArtists(List<ArtistItem> artists) {
            this.artists = artists;
        }

        /**
         * Get formatted track duration (mm:ss)
         */
        public String getFormattedDuration() {
            long minutes = durationMs / 60000;
            long seconds = (durationMs % 60000) / 1000;
            return String.format("%d:%02d", minutes, seconds);
        }
        
        /**
         * Get main artist name
         */
        public String getMainArtistName() {
            if (artists != null && !artists.isEmpty()) {
                return artists.get(0).getName();
            }
            return null;
        }
        
        /**
         * Get all artist names joined with commas
         */
        public String getAllArtistsNames() {
            if (artists == null || artists.isEmpty()) {
                return "";
            }
            
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < artists.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(artists.get(i).getName());
            }
            return sb.toString();
        }
    }
    
    /**
     * Inner class representing an artist
     */
    public static class ArtistItem {
        private String id;
        private String name;
        private String uri;
        private String href;
        private String type;
        private String spotifyUrl;
        
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

        public String getSpotifyUrl() {
            return spotifyUrl;
        }

        public void setSpotifyUrl(String spotifyUrl) {
            this.spotifyUrl = spotifyUrl;
        }
    }
    
    /**
     * Static method to parse album tracks JSON from the Spotify API
     * 
     * @param json The JSON string from Spotify API
     * @return SpotifyAlbumTracks object populated with data from the JSON
     * @throws RuntimeException if parsing fails
     */
    public static SpotifyAlbumTracks parseAlbumTracks(String json) {
        try {
            SpotifyAlbumTracks albumTracks = new SpotifyAlbumTracks();
            JsonNode rootNode = objectMapper.readTree(json);
            
            // Parse pagination metadata
            albumTracks.href = rootNode.path("href").asText();
            albumTracks.limit = rootNode.path("limit").asInt();
            albumTracks.next = rootNode.path("next").isNull() ? null : rootNode.path("next").asText();
            albumTracks.previous = rootNode.path("previous").isNull() ? null : rootNode.path("previous").asText();
            albumTracks.offset = rootNode.path("offset").asInt();
            albumTracks.total = rootNode.path("total").asInt();
            
            // Parse track items
            albumTracks.tracks = new ArrayList<>();
            JsonNode itemsNode = rootNode.path("items");
            if (itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    TrackItem track = new TrackItem();
                    
                    // Parse track details
                    track.id = itemNode.path("id").asText();
                    track.name = itemNode.path("name").asText();
                    track.uri = itemNode.path("uri").asText();
                    track.href = itemNode.path("href").asText();
                    track.type = itemNode.path("type").asText();
                    track.discNumber = itemNode.path("disc_number").asInt();
                    track.trackNumber = itemNode.path("track_number").asInt();
                    track.durationMs = itemNode.path("duration_ms").asLong();
                    track.explicit = itemNode.path("explicit").asBoolean();
                    track.isPlayable = itemNode.path("is_playable").asBoolean();
                    track.isLocal = itemNode.path("is_local").asBoolean();
                    track.previewUrl = itemNode.path("preview_url").isNull() ? null : itemNode.path("preview_url").asText();
                    track.spotifyUrl = itemNode.path("external_urls").path("spotify").asText();
                    
                    // Parse track artists
                    track.artists = new ArrayList<>();
                    JsonNode artistsNode = itemNode.path("artists");
                    if (artistsNode.isArray()) {
                        for (JsonNode artistNode : artistsNode) {
                            ArtistItem artist = new ArtistItem();
                            artist.id = artistNode.path("id").asText();
                            artist.name = artistNode.path("name").asText();
                            artist.uri = artistNode.path("uri").asText();
                            artist.href = artistNode.path("href").asText();
                            artist.type = artistNode.path("type").asText();
                            artist.spotifyUrl = artistNode.path("external_urls").path("spotify").asText();
                            track.artists.add(artist);
                        }
                    }
                    
                    albumTracks.tracks.add(track);
                }
            }
            
            return albumTracks;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Spotify album tracks JSON", e);
        }
    }

    // Getters and setters
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<TrackItem> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackItem> tracks) {
        this.tracks = tracks;
    }
    
    /**
     * Check if there are more pages of tracks
     */
    public boolean hasNextPage() {
        return next != null && !next.isEmpty();
    }
    
    /**
     * Check if there are previous pages of tracks
     */
    public boolean hasPreviousPage() {
        return previous != null && !previous.isEmpty();
    }
    
    /**
     * Get current page number (1-based)
     */
    public int getCurrentPage() {
        return (offset / limit) + 1;
    }
    
    /**
     * Get total number of pages
     */
    public int getTotalPages() {
        return (int) Math.ceil((double) total / limit);
    }
}