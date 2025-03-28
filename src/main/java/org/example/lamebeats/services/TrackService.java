package org.example.lamebeats.services;

import org.example.lamebeats.models.Track;
import org.example.lamebeats.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TrackService {

    private final TrackRepository trackRepository;

    @Autowired
    public TrackService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    public Optional<Track> getTrackById(Long id) {
        return trackRepository.findById(id);
    }

    @Transactional
    public Track saveTrack(Track track) {
        return trackRepository.save(track);
    }

    @Transactional
    public void deleteTrack(Long id) {
        trackRepository.deleteById(id);
    }

    public List<Track> findTracksByArtist(String artist) {
        return trackRepository.findByArtistContainingIgnoreCase(artist);
    }

    public List<Track> findTracksByTitle(String title) {
        return trackRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Track> findTracksByGenre(String genre) {
        return trackRepository.findByGenre(genre);
    }

    public List<Track> findTracksByAlbum(String album) {
        return trackRepository.findByAlbumContainingIgnoreCase(album);
    }

    public List<Track> findTopTracks(int minPlayCount) {
        return trackRepository.findTopTracks(minPlayCount);
    }

    public List<Track> findRecentTracks(LocalDateTime since) {
        return trackRepository.findRecentTracks(since);
    }

    public List<Track> findRecentlyAddedTracks() {
        return trackRepository.findRecentlyAddedTracks();
    }

    @Transactional
    public void incrementPlayCount(Long id) {
        trackRepository.findById(id).ifPresent(track -> {
            track.setPlayCount(track.getPlayCount() + 1);
            trackRepository.save(track);
        });
    }
}