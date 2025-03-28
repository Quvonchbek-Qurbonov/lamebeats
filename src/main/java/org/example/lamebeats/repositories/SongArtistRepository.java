package org.example.lamebeats.repositories;

import org.example.lamebeats.models.SongArtist;
import org.example.lamebeats.models.SongArtistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SongArtistRepository extends JpaRepository<SongArtist, SongArtistId> {

    // Find by song id
    List<SongArtist> findBySongId(UUID songId);

    // Find by artist id
    List<SongArtist> findByArtistId(UUID artistId);

    // Count artist appearances on songs
    @Query("SELECT sa.artistId, COUNT(sa) FROM SongArtist sa GROUP BY sa.artistId ORDER BY COUNT(sa) DESC")
    List<Object[]> countSongsByArtist(org.springframework.data.domain.Pageable pageable);

    // Count songs for a specific artist
    @Query("SELECT COUNT(sa) FROM SongArtist sa WHERE sa.artistId = :artistId")
    long countSongsByArtistId(@Param("artistId") UUID artistId);

    // Count artists for a specific song
    @Query("SELECT COUNT(sa) FROM SongArtist sa WHERE sa.songId = :songId")
    long countArtistsBySongId(@Param("songId") UUID songId);

    // Find collaborations between artists
    @Query("SELECT sa1.artistId, sa2.artistId, COUNT(sa1) FROM SongArtist sa1, SongArtist sa2 " +
            "WHERE sa1.songId = sa2.songId AND sa1.artistId < sa2.artistId " +
            "GROUP BY sa1.artistId, sa2.artistId ORDER BY COUNT(sa1) DESC")
    List<Object[]> findArtistCollaborations(org.springframework.data.domain.Pageable pageable);
}