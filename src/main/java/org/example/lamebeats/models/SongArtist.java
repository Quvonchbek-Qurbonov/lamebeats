package org.example.lamebeats.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "song_artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(SongArtistId.class)
public class SongArtist {

    @Id
    @Column(name = "song_id")
    private UUID songId;

    @Id
    @Column(name = "artist_id")
    private UUID artistId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "song_id", insertable = false, updatable = false)
    private Song song;

    @ManyToOne
    @JoinColumn(name = "artist_id", insertable = false, updatable = false)
    private Artist artist;
}