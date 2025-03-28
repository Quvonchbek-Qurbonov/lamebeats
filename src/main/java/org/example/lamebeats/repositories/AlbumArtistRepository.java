package org.example.lamebeats.repositories;

import org.example.lamebeats.models.AlbumArtist;
import org.example.lamebeats.models.AlbumArtistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlbumArtistRepository extends JpaRepository<AlbumArtist, AlbumArtistId> {

    // Find by album id
    List<AlbumArtist> findByAlbumId(UUID albumId);

    // Find by artist id
    List<AlbumArtist> findByArtistId(UUID artistId);

    // Count artist collaborations on albums
    @Query("SELECT aa.artistId, COUNT(aa) FROM AlbumArtist aa GROUP BY aa.artistId ORDER BY COUNT(aa) DESC")
    List<Object[]> countAlbumsByArtist(org.springframework.data.domain.Pageable pageable);

    // Count albums for a specific artist
    @Query("SELECT COUNT(aa) FROM AlbumArtist aa WHERE aa.artistId = :artistId")
    long countAlbumsByArtistId(@Param("artistId") UUID artistId);

    // Count artists for a specific album
    @Query("SELECT COUNT(aa) FROM AlbumArtist aa WHERE aa.albumId = :albumId")
    long countArtistsByAlbumId(@Param("albumId") UUID albumId);
}