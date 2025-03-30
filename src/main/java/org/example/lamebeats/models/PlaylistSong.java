package org.example.lamebeats.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "playlist_songs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PlaylistSongId.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PlaylistSong {

    @Id
    @Column(name = "playlist_id")
    private UUID playlistId;

    @Id
    @Column(name = "song_id")
    private UUID songId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "playlist_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"songs", "user", "hibernateLazyInitializer", "handler"})
    private Playlist playlist;

    @ManyToOne
    @JoinColumn(name = "song_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"playlists", "artists", "album", "recentTracks", "lyrics", "hibernateLazyInitializer", "handler"})
    private Song song;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void restore() {
        this.deletedAt = null;
    }
}