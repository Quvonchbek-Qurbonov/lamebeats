package org.example.lamebeats.repositories;

import org.example.lamebeats.models.ArtistGenre;
import org.example.lamebeats.models.ArtistGenreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArtistGenreRepository extends JpaRepository<ArtistGenre, ArtistGenreId> {

    // Find by artist id
    List<ArtistGenre> findByArtistId(UUID artistId);

    // Find by genre id
    List<ArtistGenre> findByGenreId(UUID genreId);

    // Count artists per genre
    @Query("SELECT ag.genreId, COUNT(ag) FROM ArtistGenre ag GROUP BY ag.genreId ORDER BY COUNT(ag) DESC")
    List<Object[]> countArtistsByGenre(org.springframework.data.domain.Pageable pageable);

    // Count genres for a specific artist
    @Query("SELECT COUNT(ag) FROM ArtistGenre ag WHERE ag.artistId = :artistId")
    long countGenresByArtistId(@Param("artistId") UUID artistId);

    // Count artists for a specific genre
    @Query("SELECT COUNT(ag) FROM ArtistGenre ag WHERE ag.genreId = :genreId")
    long countArtistsByGenreId(@Param("genreId") UUID genreId);
}