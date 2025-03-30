package org.example.lamebeats.dto;

import org.example.lamebeats.models.AlbumArtist;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlbumArtistDto {
    private UUID albumId;
    private UUID artistId;
    private String albumTitle;
    private String artistName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Static factory method to convert Entity to DTO
    public static AlbumArtistDto fromEntity(AlbumArtist albumArtist) {
        if (albumArtist == null) return null;
        
        AlbumArtistDto dto = new AlbumArtistDto();
        dto.setAlbumId(albumArtist.getAlbumId());
        dto.setArtistId(albumArtist.getArtistId());
        dto.setCreatedAt(albumArtist.getCreatedAt());
        dto.setUpdatedAt(albumArtist.getUpdatedAt());
        
        // Add album and artist details if available
        if (albumArtist.getAlbum() != null) {
            dto.setAlbumTitle(albumArtist.getAlbum().getTitle());
        }
        
        if (albumArtist.getArtist() != null) {
            dto.setArtistName(albumArtist.getArtist().getName());
        }
        
        return dto;
    }

    // Getters and setters
    public UUID getAlbumId() {
        return albumId;
    }

    public void setAlbumId(UUID albumId) {
        this.albumId = albumId;
    }

    public UUID getArtistId() {
        return artistId;
    }

    public void setArtistId(UUID artistId) {
        this.artistId = artistId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
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