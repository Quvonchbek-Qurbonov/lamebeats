package org.example.lamebeats.models;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ArtistGenreId implements Serializable {
    private UUID artistId;
    private UUID genreId;
}