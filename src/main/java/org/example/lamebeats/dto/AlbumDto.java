package org.example.lamebeats.dto;

import org.example.lamebeats.models.Album;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.Song;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AlbumDto {
    private UUID id;
    private String title;
    private LocalDate releaseDate;
    private String photo;
    private List<SongDto> songs;
    private Set<ArtistDto> artists;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private int songCount;

    // Static factory method to convert Entity to DTO with full relationships
    public static AlbumDto fromEntity(Album album) {
        if (album == null) return null;
        
        AlbumDto dto = new AlbumDto();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setPhoto(album.getPhoto());
        dto.setCreatedAt(album.getCreatedAt());
        dto.setUpdatedAt(album.getUpdatedAt());
        dto.setDeletedAt(album.getDeletedAt());
        
        // Convert songs to DTOs
        if (album.getSongs() != null && !album.getSongs().isEmpty()) {
            dto.setSongs(album.getSongs().stream()
                    .map(SongDto::fromEntitySimple)
                    .collect(Collectors.toList()));
            dto.setSongCount(album.getSongs().size());
        } else {
            dto.setSongCount(0);
        }
        
        // Convert artists to DTOs
        if (album.getArtists() != null && !album.getArtists().isEmpty()) {
            dto.setArtists(album.getArtists().stream()
                    .map(ArtistDto::fromEntitySimple)
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }
    
    // Static factory method for simplified version (without nested songs)
    public static AlbumDto fromEntitySimple(Album album) {
        if (album == null) return null;
        
        AlbumDto dto = new AlbumDto();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setPhoto(album.getPhoto());
        dto.setCreatedAt(album.getCreatedAt());
        dto.setUpdatedAt(album.getUpdatedAt());
        dto.setDeletedAt(album.getDeletedAt());
        
        // Just set song count
        if (album.getSongs() != null) {
            dto.setSongCount(album.getSongs().size());
        } else {
            dto.setSongCount(0);
        }
        
        // Convert artists to DTOs
        if (album.getArtists() != null && !album.getArtists().isEmpty()) {
            dto.setArtists(album.getArtists().stream()
                    .map(ArtistDto::fromEntitySimple)
                    .collect(Collectors.toSet()));
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<SongDto> getSongs() {
        return songs;
    }

    public void setSongs(List<SongDto> songs) {
        this.songs = songs;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }
}