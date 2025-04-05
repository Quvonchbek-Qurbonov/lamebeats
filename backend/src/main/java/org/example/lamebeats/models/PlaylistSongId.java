package org.example.lamebeats.models;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlaylistSongId implements Serializable {
    private UUID playlistId;
    private UUID songId;
}