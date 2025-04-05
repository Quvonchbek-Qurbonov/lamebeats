package org.example.lamebeats.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spotify.api")
public class SpotifyApiProperties {
    private String baseUrl;
    private Resources resources;

    public static class Resources {
        private String search;
        private String artists;
        private String albums;
        private String tracks;

        // Getters and setters
        public String getSearch() { return search; }
        public void setSearch(String search) { this.search = search; }

        public String getArtists() { return artists; }
        public void setArtists(String artists) { this.artists = artists; }

        public String getAlbums() { return albums; }
        public void setAlbums(String albums) { this.albums = albums; }

        public String getTracks() { return tracks; }
        public void setTracks(String tracks) { this.tracks = tracks; }
    }

    // Getters and setters
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public Resources getResources() { return resources; }
    public void setResources(Resources resources) { this.resources = resources; }
}
