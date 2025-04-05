package org.example.lamebeats.dto;

import org.example.lamebeats.models.Playlist;
import org.example.lamebeats.models.Song;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlaylistDto {
    private UUID id;
    private String name;
    private String description;
    private UUID userId;
    private String username;
    private String photo;
    private List<SongDto> songs;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int songCount;

    // Static factory method
    public static PlaylistDto fromEntity(Playlist playlist) {
        PlaylistDto dto = new PlaylistDto();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setDescription(playlist.getDescription());
        dto.setUserId(playlist.getUser().getId());
        dto.setUsername(playlist.getUser().getUsername());
        dto.setPhoto(playlist.getPhoto());
        dto.setCreatedAt(playlist.getCreatedAt());
        dto.setUpdatedAt(playlist.getUpdatedAt());
        
        if (playlist.getSongs() != null) {
            dto.setSongs(playlist.getSongs().stream()
                    .map(SongDto::fromEntity)
                    .collect(Collectors.toList()));
            dto.setSongCount(playlist.getSongs().size());
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

    public List<SongDto> getSongs() {
        return songs;
    }

    public void setSongs(List<SongDto> songs) {
        this.songs = songs;
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

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}