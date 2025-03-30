package org.example.lamebeats.dto;

import org.example.lamebeats.models.ArtistGenre;

import java.util.UUID;

public class ArtistGenreDto {
    private UUID artistId;
    private UUID genreId;
    private String artistName;
    private String genreTitle;
    
    // Static factory method to convert Entity to DTO
    public static ArtistGenreDto fromEntity(ArtistGenre artistGenre) {
        if (artistGenre == null) return null;
        
        ArtistGenreDto dto = new ArtistGenreDto();
        dto.setArtistId(artistGenre.getArtistId());
        dto.setGenreId(artistGenre.getGenreId());
        
        // Add artist and genre details if available
        if (artistGenre.getArtist() != null) {
            dto.setArtistName(artistGenre.getArtist().getName());
        }
        
        if (artistGenre.getGenre() != null) {
            dto.setGenreTitle(artistGenre.getGenre().getTitle());
        }
        
        return dto;
    }

    // Getters and setters
    public UUID getArtistId() {
        return artistId;
    }

    public void setArtistId(UUID artistId) {
        this.artistId = artistId;
    }

    public UUID getGenreId() {
        return genreId;
    }

    public void setGenreId(UUID genreId) {
        this.genreId = genreId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getGenreTitle() {
        return genreTitle;
    }

    public void setGenreTitle(String genreTitle) {
        this.genreTitle = genreTitle;
    }
}