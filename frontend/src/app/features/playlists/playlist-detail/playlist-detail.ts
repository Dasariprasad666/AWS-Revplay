import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Playlist } from '../../../core/services/playlist/playlist'; 
import { environment } from '../../../../environments/environment'; 

@Component({
  selector: 'app-playlist-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './playlist-detail.html',
  styleUrl: './playlist-detail.css'
})
export class PlaylistDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private playlistService = inject(Playlist);

  playlistId: number = 0;
  playlistData: any = null;
  currentSong: any = null;

  ngOnInit() {
    // 1. Grab the ID from the URL (e.g., /playlists/5)
    this.playlistId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadPlaylist();
  }

  loadPlaylist() {
    this.playlistService.getPlaylistById(this.playlistId).subscribe({
      next: (data: any) => {
        this.playlistData = data;
        console.log("Playlist loaded:", data);
      },
      error: (err: any) => console.error('Failed to load playlist', err)
    });
  }

  playSong(song: any) {
    this.currentSong = song;
  }

  removeSong(event: Event, songId: number) {
    event.stopPropagation(); // Prevent playing the song when clicking delete
    
    if(confirm('Remove this song from your playlist?')) {
      this.playlistService.removeSongFromPlaylist(this.playlistId, songId).subscribe({
        next: () => {
          // Instantly remove it from the screen without refreshing
          this.playlistData.songs = this.playlistData.songs.filter((s: any) => s.songId !== songId);
        },
        error: (err: any) => console.error('Failed to remove song', err)
      });
    }
  }

  getAudioUrl(fileName: string): string {
    return `${environment.apiUrl}/songs/play/${fileName}`;
  }

  getCoverImageUrl(fileName: string | null): string {
    if (!fileName) return 'assets/default-cover.jpg';
    return `${environment.apiUrl}/songs/image/${fileName}`;
  }
}