package com.example.demo.controller;

import com.example.demo.dto.PlaylistRequest;
import com.example.demo.dto.PlaylistResponse;
import com.example.demo.service.CurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final CurationService curationService;

    public PlaylistController(CurationService curationService) {
        this.curationService = curationService;
    }

    // 1. View Playlist and its Songs
    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable Long playlistId) {
        return ResponseEntity.ok(curationService.getPlaylistById(playlistId));
    }

    // 2. Create Playlist
    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(Authentication authentication, @RequestBody PlaylistRequest request) {
        return ResponseEntity.ok(curationService.createPlaylist(authentication.getName(), request));
    }

    // 3. Get My Playlists
    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponse>> getMyPlaylists(Authentication authentication) {
        return ResponseEntity.ok(curationService.getMyPlaylists(authentication.getName()));
    }

    // 4. Get All Public Playlists
    @GetMapping("/public")
    public ResponseEntity<List<PlaylistResponse>> getPublicPlaylists() {
        return ResponseEntity.ok(curationService.getPublicPlaylists());
    }

    // 5. Update Playlist
    @PutMapping("/{playlistId}")
    public ResponseEntity<PlaylistResponse> updatePlaylist(Authentication authentication, @PathVariable Long playlistId, @RequestBody PlaylistRequest request) {
        return ResponseEntity.ok(curationService.updatePlaylist(authentication.getName(), playlistId, request));
    }

    // 6. Delete Playlist
    @DeleteMapping("/{playlistId}")
    public ResponseEntity<String> deletePlaylist(Authentication authentication, @PathVariable Long playlistId) {
        String message = curationService.deletePlaylist(authentication.getName(), playlistId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }

    // 7. Add Song to Playlist
    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<String> addSongToPlaylist(Authentication authentication, @PathVariable Long playlistId, @PathVariable Long songId) {
        String message = curationService.addSongToPlaylist(authentication.getName(), playlistId, songId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }

    // 8. Remove Song from Playlist
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<String> removeSongFromPlaylist(Authentication authentication, @PathVariable Long playlistId, @PathVariable Long songId) {
        String message = curationService.removeSongFromPlaylist(authentication.getName(), playlistId, songId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }

    // 9. Follow / Unfollow a Public Playlist (Toggle)
    @PostMapping("/{playlistId}/follow")
    public ResponseEntity<String> toggleFollowPlaylist(Authentication authentication, @PathVariable Long playlistId) {
        String message = curationService.toggleFollowPlaylist(authentication.getName(), playlistId);
        return ResponseEntity.ok("{\"message\": \"" + message + "\"}");
    }
}