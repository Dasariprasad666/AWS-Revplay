package com.example.demo.service;

import com.example.demo.dto.AlbumDTO;
import com.example.demo.dto.SongDTO;
import com.example.demo.entity.Album;
import com.example.demo.entity.Artist;
import com.example.demo.entity.Song;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.SongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    // --- Get Single Album with Tracklist ---
    public AlbumDTO getAlbumById(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        return mapToDTOWithSongs(album);
    }

    // --- Create Album ---
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

    // --- View My Albums ---
    public List<AlbumDTO> getMyAlbums(String email) {
        Artist artist = artistRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        return albumRepository.findByArtist_ArtistId(artist.getArtistId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // --- Update Album ---
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

    // --- Delete Album (Only if no songs) ---
    public void deleteAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        if (album.getSongs() != null && !album.getSongs().isEmpty()) {
            throw new RuntimeException("Cannot delete album. Songs exist.");
        }
        albumRepository.delete(album);
    }

    // --- NEW: Add existing song to an album ---
    @Transactional
    public String addSongToAlbum(String email, Long albumId, Long songId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        // Security Check: Make sure the artist owns both the album and the song
        if (!album.getArtist().getUser().getEmail().equals(email) ||
                !song.getArtist().getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only modify your own albums and songs!");
        }

        song.setAlbum(album);
        songRepository.save(song);
        return "Song successfully added to the album!";
    }

    // --- NEW: Remove song from an album ---
    @Transactional
    public String removeSongFromAlbum(String email, Long albumId, Long songId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        // Security Check
        if (!album.getArtist().getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can only modify your own albums!");
        }

        // Unlink the song from the album
        if (song.getAlbum() != null && song.getAlbum().getAlbumId().equals(albumId)) {
            song.setAlbum(null);
            songRepository.save(song);
        }

        return "Song successfully removed from the album!";
    }

    // --- Helper Mappers ---
    private AlbumDTO mapToDTO(Album album) {
        AlbumDTO dto = new AlbumDTO();
        dto.setAlbumId(album.getAlbumId());
        dto.setTitle(album.getTitle());
        dto.setDescription(album.getDescription());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setCoverImageUrl(album.getCoverImageUrl());
        return dto;
    }

    private AlbumDTO mapToDTOWithSongs(Album album) {
        AlbumDTO dto = mapToDTO(album);
        if (album.getSongs() != null) {
            List<SongDTO> songDTOs = album.getSongs().stream().map(this::mapSongToDTO).collect(Collectors.toList());
            dto.setSongs(songDTOs);
        }
        return dto;
    }

    private SongDTO mapSongToDTO(Song song) {
        SongDTO dto = new SongDTO();
        dto.setSongId(song.getSongId());
        dto.setTitle(song.getTitle());
        dto.setGenre(song.getGenre());
        dto.setDuration(song.getDuration());
        dto.setPlayCount(song.getPlayCount());
        dto.setAudioFileUrl(song.getAudioFileUrl());
        dto.setCoverImageUrl(song.getCoverImageUrl());
        if (song.getArtist() != null && song.getArtist().getUser() != null) {
            dto.setArtistName(song.getArtist().getUser().getName());
        }
        return dto;
    }
}