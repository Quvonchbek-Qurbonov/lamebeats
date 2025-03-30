package org.example.lamebeats.dto;

import org.example.lamebeats.models.SongArtist;

import java.time.LocalDateTime;
import java.util.UUID;

public class SongArtistDto {
    private UUID songId;
    private UUID artistId;
    private String songTitle;
    private String artistName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Static factory method to convert Entity to DTO
    public static SongArtistDto fromEntity(SongArtist songArtist) {
        SongArtistDto dto = new SongArtistDto();
        dto.setSongId(songArtist.getSongId());
        dto.setArtistId(songArtist.getArtistId());
        
        // Add song and artist details if available
        if (songArtist.getSong() != null) {
            dto.setSongTitle(songArtist.getSong().getTitle());
        }
        
        if (songArtist.getArtist() != null) {
            dto.setArtistName(songArtist.getArtist().getName());
        }
        
        dto.setCreatedAt(songArtist.getCreatedAt());
        dto.setUpdatedAt(songArtist.getUpdatedAt());
        
        return dto;
    }

    // Getters and setters
    public UUID getSongId() {
        return songId;
    }

    public void setSongId(UUID songId) {
        this.songId = songId;
    }

    public UUID getArtistId() {
        return artistId;
    }

    public void setArtistId(UUID artistId) {
        this.artistId = artistId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}