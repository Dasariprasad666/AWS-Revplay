package com.example.demo.service;


import com.example.demo.dto.AlbumDTO;
import com.example.demo.entity.Album;
import com.example.demo.entity.Artist;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;

    public AlbumService(AlbumRepository albumRepository,
                        ArtistRepository artistRepository,
                        SongRepository songRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
    }

    // Create Album
    public AlbumDTO createAlbum(String email, AlbumDTO dto) {

        Artist artist = artistRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        Album album = new Album();
        album.setTitle(dto.getTitle());
        album.setDescription(dto.getDescription());
        album.setReleaseDate(dto.getReleaseDate());
        album.setCoverImageUrl(dto.getCoverImageUrl());
        album.setArtist(artist);

        albumRepository.save(album);

        dto.setAlbumId(album.getAlbumId());
        return dto;
    }

    // View My Albums
    public List<AlbumDTO> getMyAlbums(String email) {

        Artist artist = artistRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        return albumRepository.findByArtist_ArtistId(artist.getArtistId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // Update Album
    public AlbumDTO updateAlbum(Long albumId, AlbumDTO dto) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        album.setTitle(dto.getTitle());
        album.setDescription(dto.getDescription());
        album.setReleaseDate(dto.getReleaseDate());
        album.setCoverImageUrl(dto.getCoverImageUrl());

        albumRepository.save(album);

        return mapToDTO(album);
    }

    // Delete Album (Only if no songs)
    public void deleteAlbum(Long albumId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        if (album.getSongs() != null && !album.getSongs().isEmpty()) {
            throw new RuntimeException("Cannot delete album. Songs exist.");
        }

        albumRepository.delete(album);
    }

    private AlbumDTO mapToDTO(Album album) {
        AlbumDTO dto = new AlbumDTO();
        dto.setAlbumId(album.getAlbumId());
        dto.setTitle(album.getTitle());
        dto.setDescription(album.getDescription());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setCoverImageUrl(album.getCoverImageUrl());
        return dto;
    }
}
