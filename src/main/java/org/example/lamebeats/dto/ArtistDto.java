package org.example.lamebeats.dto;

import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.Genre;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArtistDto {
    private UUID id;
    private String name;
    private String photo;
    private String spotifyId;
    private Set<GenreDto> genres;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Static factory method to convert Entity to DTO with genres
    public static ArtistDto fromEntity(Artist artist) {
        if (artist == null) return null;

        ArtistDto dto = new ArtistDto();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setPhoto(artist.getPhoto());
        dto.setSpotifyId(artist.getSpotifyId());
        dto.setCreatedAt(artist.getCreatedAt());
        dto.setUpdatedAt(artist.getUpdatedAt());
        dto.setDeletedAt(artist.getDeletedAt());

        // Convert genres to DTOs
        if (artist.getGenres() != null && !artist.getGenres().isEmpty()) {
            dto.setGenres(artist.getGenres().stream()
                    .map(GenreDto::fromEntitySimple)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    // Static factory method for simplified version (without nested objects)
    public static ArtistDto fromEntitySimple(Artist artist) {
        if (artist == null) return null;

        ArtistDto dto = new ArtistDto();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setPhoto(artist.getPhoto());
        dto.setCreatedAt(artist.getCreatedAt());
        dto.setUpdatedAt(artist.getUpdatedAt());
        dto.setDeletedAt(artist.getDeletedAt());

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public Set<GenreDto> getGenres() {
        return genres;
    }

    public void setGenres(Set<GenreDto> genres) {
        this.genres = genres;
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