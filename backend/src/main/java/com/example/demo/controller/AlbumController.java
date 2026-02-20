package com.example.demo.controller;

import com.example.demo.dto.AlbumDTO;
import com.example.demo.service.AlbumService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    // --- View a single album and its tracklist (Public) ---
    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.getAlbumById(albumId));
    }

    // --- Create Album (Artist Only) ---
    @PostMapping
    public ResponseEntity<AlbumDTO> createAlbum(@RequestBody AlbumDTO dto,
                                                Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(albumService.createAlbum(email, dto));
    }

    // --- View My Albums (Artist Only) ---
    @GetMapping("/my")
    public ResponseEntity<List<AlbumDTO>> getMyAlbums(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(albumService.getMyAlbums(email));
    }

    // --- Update Album (Artist Only) ---
    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDTO> updateAlbum(@PathVariable Long albumId,
                                                @RequestBody AlbumDTO dto) {
        return ResponseEntity.ok(albumService.updateAlbum(albumId, dto));
    }

    // --- Delete Album (Artist Only) ---
    @DeleteMapping("/{albumId}")
    public ResponseEntity<String> deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
        return ResponseEntity.ok("{\"message\": \"Album deleted successfully\"}");
    }

    // --- NEW: Add Song to Album (Artist Only) ---
    @PostMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<String> addSongToAlbum(Authentication authentication,
                                                 @PathVariable Long albumId,
                                                 @PathVariable Long songId) {
        String message = albumService.addSongToAlbum(authentication.getName(), albumId, songId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }

    // --- NEW: Remove Song from Album (Artist Only) ---
    @DeleteMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<String> removeSongFromAlbum(Authentication authentication,
                                                      @PathVariable Long albumId,
                                                      @PathVariable Long songId) {
        String message = albumService.removeSongFromAlbum(authentication.getName(), albumId, songId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }
}