package org.example.lamebeats.dto;

import org.example.lamebeats.models.Song;
import java.util.UUID;

public class SongDto {
    private UUID id;
    private String title;
    private String artist;
    private String album;
    // Other fields as needed...

    public static SongDto fromEntity(Song song) {
        SongDto dto = new SongDto();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        // Set other fields as needed
        return dto;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}