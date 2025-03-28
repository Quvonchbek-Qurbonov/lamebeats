package org.example.lamebeats.repositories;

import org.example.lamebeats.models.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    List<Track> findByArtistContainingIgnoreCase(String artist);

    List<Track> findByTitleContainingIgnoreCase(String title);

    List<Track> findByGenre(String genre);

    List<Track> findByAlbumContainingIgnoreCase(String album);

    @Query("SELECT t FROM Track t WHERE t.playCount > :minPlayCount ORDER BY t.playCount DESC")
    List<Track> findTopTracks(@Param("minPlayCount") Integer minPlayCount);

    @Query("SELECT t FROM Track t WHERE t.releaseDate > :date ORDER BY t.releaseDate DESC")
    List<Track> findRecentTracks(@Param("date") LocalDateTime date);

    @Query("SELECT t FROM Track t ORDER BY t.createdAt DESC")
    List<Track> findRecentlyAddedTracks();
}