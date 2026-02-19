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

    // Create Album
    @PostMapping
    public ResponseEntity<AlbumDTO> createAlbum(@RequestBody AlbumDTO dto,
                                                Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(albumService.createAlbum(email, dto));
    }

    // View My Albums
    @GetMapping("/my")
    public ResponseEntity<List<AlbumDTO>> getMyAlbums(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(albumService.getMyAlbums(email));
    }

    // Update Album
    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDTO> updateAlbum(@PathVariable Long albumId,
                                                @RequestBody AlbumDTO dto) {
        return ResponseEntity.ok(albumService.updateAlbum(albumId, dto));
    }

    // Delete Album
    @DeleteMapping("/{albumId}")
    public ResponseEntity<String> deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
        return ResponseEntity.ok("Album deleted successfully");
    }
}
