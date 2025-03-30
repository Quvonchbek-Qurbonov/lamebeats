package org.example.lamebeats.dto;

import org.example.lamebeats.enums.Language;
import org.example.lamebeats.models.Lyrics;

import java.time.LocalDateTime;
import java.util.UUID;

public class LyricsDto {
    private UUID id;
    private UUID songId;
    private String songTitle;
    private String content;
    private Language language;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    // Static factory method to convert Entity to DTO
    public static LyricsDto fromEntity(Lyrics lyrics) {
        if (lyrics == null) return null;
        
        LyricsDto dto = new LyricsDto();
        dto.setId(lyrics.getId());
        
        if (lyrics.getSong() != null) {
            dto.setSongId(lyrics.getSong().getId());
            dto.setSongTitle(lyrics.getSong().getTitle());
        }
        
        dto.setContent(lyrics.getContent());
        dto.setLanguage(lyrics.getLanguage());
        dto.setCreatedAt(lyrics.getCreatedAt());
        dto.setUpdatedAt(lyrics.getUpdatedAt());
        dto.setDeletedAt(lyrics.getDeletedAt());
        
        return dto;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}