package org.example.lamebeats.models;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SongArtistId implements Serializable {
    private UUID songId;
    private UUID artistId;
}