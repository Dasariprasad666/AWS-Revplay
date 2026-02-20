package com.example.demo.controller;

import com.example.demo.dto.SongDTO;
import com.example.demo.service.SongService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    // --- PUBLIC ENDPOINTS (Listeners) ---
    @GetMapping
    public ResponseEntity<List<SongDTO>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    @GetMapping("/{songId}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable Long songId) {
        return ResponseEntity.ok(songService.getSongById(songId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SongDTO>> searchSongs(@RequestParam String title) {
        return ResponseEntity.ok(songService.searchSongsByTitle(title));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SongDTO>> filterSongs(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false, name = "artist") String artistName,
            @RequestParam(required = false, name = "album") String albumName,
            @RequestParam(required = false, name = "year") Integer releaseYear) {
        return ResponseEntity.ok(songService.filterSongs(genre, artistName, albumName, releaseYear));
    }

    // --- SECURED ENDPOINTS (Artists Only) ---

    // View your own uploaded songs
    @GetMapping("/my-songs")
    public ResponseEntity<List<SongDTO>> getMyUploadedSongs(Authentication authentication) {
        return ResponseEntity.ok(songService.getMyUploadedSongs(authentication.getName()));
    }

    // Upload a new song (Requires Multipart Form Data for files)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SongDTO> uploadSong(
            Authentication authentication,
            @RequestParam("title") String title,
            @RequestParam("genre") String genre,
            @RequestParam("duration") Integer duration,
            @RequestParam(value = "visibility", defaultValue = "PUBLIC") String visibility,
            @RequestParam(value = "albumId", required = false) Long albumId,
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {

        return ResponseEntity.ok(songService.uploadSong(
                authentication.getName(), title, genre, duration, visibility, albumId, audioFile, coverImage));
    }

    // Update song details
    @PutMapping("/{songId}")
    public ResponseEntity<SongDTO> updateSong(
            Authentication authentication,
            @PathVariable Long songId,
            @RequestParam("title") String title,
            @RequestParam("genre") String genre,
            @RequestParam("visibility") String visibility) {

        return ResponseEntity.ok(songService.updateSong(authentication.getName(), songId, title, genre, visibility));
    }

    // Delete a song
    @DeleteMapping("/{songId}")
    public ResponseEntity<String> deleteSong(Authentication authentication, @PathVariable Long songId) {
        songService.deleteSong(authentication.getName(), songId);
        return ResponseEntity.ok("{\"message\": \"Song deleted successfully.\"}");
    }
}