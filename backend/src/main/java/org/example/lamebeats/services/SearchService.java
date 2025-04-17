package org.example.lamebeats.services;

import org.example.lamebeats.models.Album;
import org.example.lamebeats.models.Artist;
import org.example.lamebeats.models.Song;
import org.example.lamebeats.repositories.AlbumRepository;
import org.example.lamebeats.repositories.ArtistRepository;
import org.example.lamebeats.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public SearchService(SongRepository songRepository, AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    public List<Song> searchSongs(String title, int limit) {
        return songRepository.searchByTitleOrArtist(title).stream()
                .limit(limit)
                .toList();
    }

    public List<Album> searchAlbums(String title, int limit) {
        return albumRepository.findActiveByTitleContaining(title).stream()
                .limit(limit)
                .toList();
    }

    public List<Artist> searchArtists(String name, int limit) {
        return artistRepository.findActiveByNameContaining(name).stream()
                .limit(limit)
                .toList();
    }
}