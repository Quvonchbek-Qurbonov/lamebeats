package org.example.lamebeats.dto;

import org.example.lamebeats.models.RecentTrack;

import java.time.LocalDateTime;
import java.util.UUID;

public class RecentTrackDto {
    private UUID id;
    private UUID userId;
    private String username;
    private UUID songId;
    private String songTitle;
    private String artistName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Static factory method to convert Entity to DTO
    public static RecentTrackDto fromEntity(RecentTrack recentTrack) {
        if (recentTrack == null) return null;
        
        RecentTrackDto dto = new RecentTrackDto();
        dto.setId(recentTrack.getId());
        dto.setCreatedAt(recentTrack.getCreatedAt());
        dto.setUpdatedAt(recentTrack.getUpdatedAt());
        
        // Add user details if available
        if (recentTrack.getUser() != null) {
            dto.setUserId(recentTrack.getUser().getId());
            dto.setUsername(recentTrack.getUser().getUsername());
        }
        
        // Add song details if available
        if (recentTrack.getSong() != null) {
            dto.setSongId(recentTrack.getSong().getId());
            dto.setSongTitle(recentTrack.getSong().getTitle());
            
            // Try to get artist name
            if (recentTrack.getSong().getArtists() != null && !recentTrack.getSong().getArtists().isEmpty()) {
                dto.setArtistName(recentTrack.getSong().getArtists().iterator().next().getName());
            }
        }
        
        return dto;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getSongId() {
        return songId;
    }

    public void setSongId(UUID songId) {
        this.songId = songId;
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