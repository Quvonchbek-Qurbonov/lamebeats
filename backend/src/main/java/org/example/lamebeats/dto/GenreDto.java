package org.example.lamebeats.dto;

import org.example.lamebeats.models.Genre;

import java.time.LocalDateTime;
import java.util.UUID;

public class GenreDto {
    private UUID id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    // Static factory method to convert Entity to DTO
    public static GenreDto fromEntity(Genre genre) {
        if (genre == null) return null;
        
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setTitle(genre.getTitle());
        dto.setCreatedAt(genre.getCreatedAt());
        dto.setUpdatedAt(genre.getUpdatedAt());
        dto.setDeletedAt(genre.getDeletedAt());
        
        return dto;
    }
    
    // Static factory method for simplified version
    public static GenreDto fromEntitySimple(Genre genre) {
        return fromEntity(genre);
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