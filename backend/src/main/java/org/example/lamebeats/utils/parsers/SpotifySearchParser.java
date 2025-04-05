package org.example.lamebeats.utils.parsers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpotifySearchParser {

    /**
     * Parse the Spotify API search response
     * 
     * @param responseJson The JSON response string from Spotify API
     * @return SearchResult object containing all parsed data
     */
    public static SearchResult parseSearchResponse(String responseJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseJson, SearchResult.class);
        } catch (Exception e) {
            System.err.println("Error parsing Spotify search response: " + e.getMessage());
            return new SearchResult();
        }
    }

    /**
     * Main result class containing all search results
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchResult {
        private TrackResults tracks;
        private ArtistResults artists;
        private AlbumResults albums;
        private PlaylistResults playlists;

        public TrackResults getTracks() {
            return tracks;
        }

        public void setTracks(TrackResults tracks) {
            this.tracks = tracks;
        }

        public ArtistResults getArtists() {
            return artists;
        }

        public void setArtists(ArtistResults artists) {
            this.artists = artists;
        }

        public AlbumResults getAlbums() {
            return albums;
        }

        public void setAlbums(AlbumResults albums) {
            this.albums = albums;
        }

        public PlaylistResults getPlaylists() {
            return playlists;
        }

        public void setPlaylists(PlaylistResults playlists) {
            this.playlists = playlists;
        }

        public boolean hasTracks() {
            return tracks != null && tracks.getItems() != null && !tracks.getItems().isEmpty();
        }

        public boolean hasArtists() {
            return artists != null && artists.getItems() != null && !artists.getItems().isEmpty();
        }

        public boolean hasAlbums() {
            return albums != null && albums.getItems() != null && !albums.getItems().isEmpty();
        }

        public boolean hasPlaylists() {
            return playlists != null && playlists.getItems() != null && !playlists.getItems().isEmpty();
        }
    }

    /**
     * Base results class with common pagination fields
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class BaseResults<T> {
        private String href;
        private int limit;
        private String next;
        private int offset;
        private String previous;
        private int total;
        private List<T> items;

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

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<T> getItems() {
            return items != null ? items : new ArrayList<>();
        }

        public void setItems(List<T> items) {
            this.items = items;
        }
    }

    /**
     * Track-specific results container
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrackResults extends BaseResults<Track> {
    }

    /**
     * Artist-specific results container
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArtistResults extends BaseResults<Artist> {
    }

    /**
     * Album-specific results container
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AlbumResults extends BaseResults<Album> {
    }

    /**
     * Playlist-specific results container
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaylistResults extends BaseResults<Playlist> {
    }

    /**
     * Track model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Track {
        private String id;
        private String name;
        private int duration_ms;
        private boolean explicit;
        private Map<String, String> external_urls;
        private String uri;
        private int popularity;
        private String preview_url;
        private Album album;
        private List<Artist> artists;
        private int disc_number;
        private int track_number;

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

        public int getDuration_ms() {
            return duration_ms;
        }

        public void setDuration_ms(int duration_ms) {
            this.duration_ms = duration_ms;
        }

        public boolean isExplicit() {
            return explicit;
        }

        public void setExplicit(boolean explicit) {
            this.explicit = explicit;
        }

        public Map<String, String> getExternal_urls() {
            return external_urls;
        }

        public void setExternal_urls(Map<String, String> external_urls) {
            this.external_urls = external_urls;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public int getPopularity() {
            return popularity;
        }

        public void setPopularity(int popularity) {
            this.popularity = popularity;
        }

        public String getPreview_url() {
            return preview_url;
        }

        public void setPreview_url(String preview_url) {
            this.preview_url = preview_url;
        }

        public Album getAlbum() {
            return album;
        }

        public void setAlbum(Album album) {
            this.album = album;
        }

        public List<Artist> getArtists() {
            return artists != null ? artists : new ArrayList<>();
        }

        public void setArtists(List<Artist> artists) {
            this.artists = artists;
        }

        public int getDisc_number() {
            return disc_number;
        }

        public void setDisc_number(int disc_number) {
            this.disc_number = disc_number;
        }

        public int getTrack_number() {
            return track_number;
        }

        public void setTrack_number(int track_number) {
            this.track_number = track_number;
        }
    }

    /**
     * Artist model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Artist {
        private String id;
        private String name;
        private Map<String, String> external_urls;
        private String uri;
        private List<String> genres;
        private List<Image> images;
        private int popularity;
        private Followers followers;

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

        public Map<String, String> getExternal_urls() {
            return external_urls;
        }

        public void setExternal_urls(Map<String, String> external_urls) {
            this.external_urls = external_urls;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public List<String> getGenres() {
            return genres != null ? genres : new ArrayList<>();
        }

        public void setGenres(List<String> genres) {
            this.genres = genres;
        }

        public List<Image> getImages() {
            return images != null ? images : new ArrayList<>();
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public int getPopularity() {
            return popularity;
        }

        public void setPopularity(int popularity) {
            this.popularity = popularity;
        }

        public Followers getFollowers() {
            return followers;
        }

        public void setFollowers(Followers followers) {
            this.followers = followers;
        }
    }

    /**
     * Album model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Album {
        private String id;
        private String name;
        private String album_type;
        private int total_tracks;
        private Map<String, String> external_urls;
        private String uri;
        private List<Image> images;
        private String release_date;
        private String release_date_precision;
        private List<Artist> artists;

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

        public String getAlbum_type() {
            return album_type;
        }

        public void setAlbum_type(String album_type) {
            this.album_type = album_type;
        }

        public int getTotal_tracks() {
            return total_tracks;
        }

        public void setTotal_tracks(int total_tracks) {
            this.total_tracks = total_tracks;
        }

        public Map<String, String> getExternal_urls() {
            return external_urls;
        }

        public void setExternal_urls(Map<String, String> external_urls) {
            this.external_urls = external_urls;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public List<Image> getImages() {
            return images != null ? images : new ArrayList<>();
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public String getRelease_date() {
            return release_date;
        }

        public void setRelease_date(String release_date) {
            this.release_date = release_date;
        }

        public String getRelease_date_precision() {
            return release_date_precision;
        }

        public void setRelease_date_precision(String release_date_precision) {
            this.release_date_precision = release_date_precision;
        }

        public List<Artist> getArtists() {
            return artists != null ? artists : new ArrayList<>();
        }

        public void setArtists(List<Artist> artists) {
            this.artists = artists;
        }
    }

    /**
     * Playlist model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Playlist {
        private String id;
        private String name;
        private String description;
        private Map<String, String> external_urls;
        private String uri;
        private boolean collaborative;
        private List<Image> images;
        private PlaylistOwner owner;
        private PlaylistTracks tracks;
        private String snapshot_id;

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<String, String> getExternal_urls() {
            return external_urls;
        }

        public void setExternal_urls(Map<String, String> external_urls) {
            this.external_urls = external_urls;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public boolean isCollaborative() {
            return collaborative;
        }

        public void setCollaborative(boolean collaborative) {
            this.collaborative = collaborative;
        }

        public List<Image> getImages() {
            return images != null ? images : new ArrayList<>();
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public PlaylistOwner getOwner() {
            return owner;
        }

        public void setOwner(PlaylistOwner owner) {
            this.owner = owner;
        }

        public PlaylistTracks getTracks() {
            return tracks;
        }

        public void setTracks(PlaylistTracks tracks) {
            this.tracks = tracks;
        }

        public String getSnapshot_id() {
            return snapshot_id;
        }

        public void setSnapshot_id(String snapshot_id) {
            this.snapshot_id = snapshot_id;
        }
    }

    /**
     * Playlist owner model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaylistOwner {
        private String id;
        private String display_name;
        private Map<String, String> external_urls;
        private String uri;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDisplay_name() {
            return display_name;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }

        public Map<String, String> getExternal_urls() {
            return external_urls;
        }

        public void setExternal_urls(Map<String, String> external_urls) {
            this.external_urls = external_urls;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }

    /**
     * Playlist tracks model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaylistTracks {
        private String href;
        private int total;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    /**
     * Followers model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Followers {
        private String href;
        private int total;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    /**
     * Image model
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        private String url;
        private Integer height;
        private Integer width;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }
    }
}