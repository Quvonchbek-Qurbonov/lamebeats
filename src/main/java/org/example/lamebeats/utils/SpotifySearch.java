package org.example.lamebeats.utils;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.example.lamebeats.services.AlbumService;
import org.example.lamebeats.services.ArtistService;
import org.example.lamebeats.services.SongService;
import org.example.lamebeats.services.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SpotifySearch {

    @Autowired
    private SpotifyService spotifyService;
    
    @Autowired
    private SongService songService;
    
    @Autowired
    private AlbumService albumService;
    
    @Autowired
    private ArtistService artistService;


    


    /**
     * Response class for search endpoint
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SearchResponse {
        private SearchResponseData data;
        private Integer limit;
        private Integer total;
        private Integer pages;
        private Integer page;
        private String error;
        
        public static SearchResponse error(String errorMessage) {
            SearchResponse response = new SearchResponse();
            response.setError(errorMessage);
            return response;
        }

        public SearchResponseData getData() {
            return data;
        }

        public void setData(SearchResponseData data) {
            this.data = data;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getPages() {
            return pages;
        }

        public void setPages(Integer pages) {
            this.pages = pages;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    /**
     * Data container for search results
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SearchResponseData {
        private List<SongDto> songs;
        private List<AlbumDto> albums;
        private List<ArtistDto> artists;

        public List<SongDto> getSongs() {
            return songs;
        }

        public void setSongs(List<SongDto> songs) {
            this.songs = songs;
        }

        public List<AlbumDto> getAlbums() {
            return albums;
        }

        public void setAlbums(List<AlbumDto> albums) {
            this.albums = albums;
        }

        public List<ArtistDto> getArtists() {
            return artists;
        }

        public void setArtists(List<ArtistDto> artists) {
            this.artists = artists;
        }
    }

    /**
     * DTO for Song objects
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SongDto {
        private String id;
        private String spotifyId;
        private String title;
        private AlbumDto album;
        private Integer duration;
        private List<ArtistDto> artists;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSpotifyId() {
            return spotifyId;
        }

        public void setSpotifyId(String spotifyId) {
            this.spotifyId = spotifyId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public AlbumDto getAlbum() {
            return album;
        }

        public void setAlbum(AlbumDto album) {
            this.album = album;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public List<ArtistDto> getArtists() {
            return artists;
        }

        public void setArtists(List<ArtistDto> artists) {
            this.artists = artists;
        }
    }

    /**
     * DTO for Album objects
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AlbumDto {
        private String id;
        private String spotifyId;
        private String name;
        private String releaseDate;
        private List<String> images;
        private List<ArtistDto> artists;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSpotifyId() {
            return spotifyId;
        }

        public void setSpotifyId(String spotifyId) {
            this.spotifyId = spotifyId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public List<ArtistDto> getArtists() {
            return artists;
        }

        public void setArtists(List<ArtistDto> artists) {
            this.artists = artists;
        }
    }

    /**
     * DTO for Artist objects
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ArtistDto {
        private String id;
        private String spotifyId;
        private String name;
        private List<String> genres;
        private List<String> images;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSpotifyId() {
            return spotifyId;
        }

        public void setSpotifyId(String spotifyId) {
            this.spotifyId = spotifyId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getGenres() {
            return genres;
        }

        public void setGenres(List<String> genres) {
            this.genres = genres;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }
}