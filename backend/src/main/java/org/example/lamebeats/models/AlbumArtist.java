package org.example.lamebeats.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "album_artist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(AlbumArtistId.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AlbumArtist {

    @Id
    @Column(name = "album_id")
    private UUID albumId;

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
    @JoinColumn(name = "album_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"artists", "songs", "hibernateLazyInitializer", "handler"})
    private Album album;

    @ManyToOne
    @JoinColumn(name = "artist_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"albums", "songs", "genres", "hibernateLazyInitializer", "handler"})
    private Artist artist;
}