package org.example.lamebeats.models;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AlbumArtistId implements Serializable {
    private UUID albumId;
    private UUID artistId;
}