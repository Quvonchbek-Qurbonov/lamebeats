package org.example.lamebeats.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "artist_genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ArtistGenreId.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ArtistGenre {

    @Id
    @Column(name = "artist_id")
    private UUID artistId;

    @Id
    @Column(name = "genre_id")
    private UUID genreId;

    @ManyToOne
    @JoinColumn(name = "artist_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"genres", "albums", "songs", "hibernateLazyInitializer", "handler"})
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "genre_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"artists", "hibernateLazyInitializer", "handler"})
    private Genre genre;
}