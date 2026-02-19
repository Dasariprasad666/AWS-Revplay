package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Artist;
import com.example.demo.entity.Song;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ArtistRepository;
import com.example.demo.repository.SongRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public ArtistService(ArtistRepository artistRepository, UserRepository userRepository, SongRepository songRepository) {
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    @Transactional
    public String setupOrUpdateArtistProfile(String email, ArtistProfileRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!"ARTIST".equals(user.getRole())) {
            user.setRole("ARTIST");
            userRepository.save(user);
        }

        Artist artist = artistRepository.findByUser(user).orElseGet(Artist::new);
        artist.setUser(user);
        artist.setBio(request.getBio());
        artist.setGenre(request.getGenre());
        artist.setSocialLinks(request.getSocialLinks());
        artist.setBannerImageUrl(request.getBannerImageUrl());

        artistRepository.save(artist);
        return "Artist profile successfully updated!";
    }

    public ArtistPublicResponse getPublicProfile(Long artistId) {
        Artist artist = artistRepository.findById(artistId).orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
        return mapToPublicResponse(artist);
    }

    public ArtistPublicResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Artist artist = artistRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Profile not setup"));
        return mapToPublicResponse(artist);
    }

    public ArtistStatsResponse getArtistAnalytics(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Artist artist = artistRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Artist profile not found."));

        List<Song> artistSongs = songRepository.findByArtist(artist);
        ArtistStatsResponse response = new ArtistStatsResponse();
        response.setArtistName(user.getName());
        response.setGenre(artist.getGenre());

        if (artistSongs != null && !artistSongs.isEmpty()) {
            response.setTotalSongsUploaded(artistSongs.size());
            response.setTotalAllTimePlays(artistSongs.stream().mapToLong(s -> s.getPlayCount() != null ? s.getPlayCount() : 0).sum());

            // Get Top Songs sorted by play count descending
            List<SongPerformanceDTO> topSongs = artistSongs.stream()
                    .sorted((s1, s2) -> Long.compare(s2.getPlayCount() != null ? s2.getPlayCount() : 0, s1.getPlayCount() != null ? s1.getPlayCount() : 0))
                    .limit(10) // Show top 10
                    .map(song -> {
                        SongPerformanceDTO dto = new SongPerformanceDTO();
                        dto.setSongId(song.getSongId());
                        dto.setTitle(song.getTitle());
                        dto.setPlayCount(song.getPlayCount() != null ? song.getPlayCount() : 0);
                        return dto;
                    }).collect(Collectors.toList());

            response.setTopSongs(topSongs);
        } else {
            response.setTotalSongsUploaded(0);
            response.setTotalAllTimePlays(0L);
        }

        return response;
    }

    private ArtistPublicResponse mapToPublicResponse(Artist artist) {
        ArtistPublicResponse response = new ArtistPublicResponse();
        response.setArtistId(artist.getArtistId());
        response.setName(artist.getUser().getName());
        response.setProfilePictureUrl(artist.getUser().getProfilePictureUrl());
        response.setBio(artist.getBio());
        response.setGenre(artist.getGenre());
        response.setBannerImageUrl(artist.getBannerImageUrl());
        response.setSocialLinks(artist.getSocialLinks());
        return response;
    }
}