package org.example.lamebeats.models;

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
public class ArtistGenre {

    @Id
    @Column(name = "artist_id")
    private UUID artistId;

    @Id
    @Column(name = "genre_id")
    private UUID genreId;

    @ManyToOne
    @JoinColumn(name = "artist_id", insertable = false, updatable = false)
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "genre_id", insertable = false, updatable = false)
    private Genre genre;
}