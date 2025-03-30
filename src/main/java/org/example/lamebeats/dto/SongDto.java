package org.example.lamebeats.dto;

import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.Song;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SongDto {
    private UUID id;
    private String title;
    private UUID albumId;
    private String albumTitle;
    private Integer duration;
    private String genre;
    private String fileUrl;
    private Set<ArtistDto> artists;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Static factory method to convert Entity to DTO
    public static SongDto fromEntity(Song song) {
        SongDto dto = new SongDto();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());

        // Handle album nullable relationship
        if (song.getAlbum() != null) {
            dto.setAlbumId(song.getAlbum().getId());
            dto.setAlbumTitle(song.getAlbum().getTitle());
        }

        dto.setDuration(song.getDuration());
        dto.setGenre(song.getGenre());
        dto.setFileUrl(song.getFileUrl());
        dto.setCreatedAt(song.getCreatedAt());
        dto.setUpdatedAt(song.getUpdatedAt());

        // Convert artists to ArtistDto
        if (song.getArtists() != null && !song.getArtists().isEmpty()) {
            dto.setArtists(song.getArtists().stream()
                    .map(ArtistDto::fromEntity)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    // Static factory method to create a simple version without nested objects
    public static SongDto fromEntitySimple(Song song) {
        SongDto dto = new SongDto();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());

        // Only set IDs, not full objects
        if (song.getAlbum() != null) {
            dto.setAlbumId(song.getAlbum().getId());
            dto.setAlbumTitle(song.getAlbum().getTitle());
        }

        dto.setDuration(song.getDuration());
        dto.setGenre(song.getGenre());
        dto.setFileUrl(song.getFileUrl());
        dto.setCreatedAt(song.getCreatedAt());
        dto.setUpdatedAt(song.getUpdatedAt());

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

    public UUID getAlbumId() {
        return albumId;
    }

    public void setAlbumId(UUID albumId) {
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Set<ArtistDto> getArtists() {
        return artists;
    }

    public void setArtists(Set<ArtistDto> artists) {
        this.artists = artists;
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