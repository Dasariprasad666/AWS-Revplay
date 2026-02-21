import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms'; 
import { AuthService } from '../../core/services/auth'; 
import { Song } from '../../core/services/song/song'; 
import { environment } from '../../../environments/environment'; 

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule], 
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  private authService = inject(AuthService);
  private songService = inject(Song);
  private router = inject(Router);

  userName: string = '';
  userRole: string = '';
  songs: any[] = []; 
  
  currentSong: any = null;

  searchQuery: string = '';
  selectedGenre: string = '';

  likedSongIds: Set<number> = new Set<number>();

  ngOnInit() {
    const storedName = this.authService.getUserName();
    this.userName = (storedName && storedName !== 'null') ? storedName : 'User';
    this.userRole = this.authService.getRole() || 'USER';
    this.fetchSongs();
    this.fetchLikedSongs();
  }

  fetchSongs() {
    this.songService.getAllSongs().subscribe({
      next: (data: any[]) => {
        this.songs = data;
        console.log('Successfully loaded songs:', this.songs);
      },
      error: (err: any) => console.error('Failed to load songs:', err)
    });
  }

  fetchLikedSongs() {
    this.songService.getLikedSongs().subscribe({
      next: (data: any[]) => {
        this.likedSongIds = new Set(data.map((song: any) => song.songId));
      },
      error: (err: any) => console.error('Failed to load liked songs:', err)
    });
  }

  toggleLike(event: Event, songId: number) {
    event.stopPropagation(); 
    
    this.songService.toggleLike(songId).subscribe({
      next: (isLiked: boolean) => {
        if (isLiked) {
          this.likedSongIds.add(songId);
        } else {
          this.likedSongIds.delete(songId);
        }
      },
      error: (err: any) => console.error('Failed to toggle like:', err)
    });
  }

  isLiked(songId: number): boolean {
    return this.likedSongIds.has(songId);
  }

  onSearch() {
    if (this.searchQuery.trim() === '') {
      this.fetchSongs(); 
      return;
    }
    this.songService.searchSongsByTitle(this.searchQuery).subscribe({
      next: (data: any[]) => this.songs = data,
      error: (err: any) => console.error('Search failed:', err)
    });
  }

  onFilterChange() {
    if (this.selectedGenre === '') {
      this.fetchSongs(); 
      return;
    }
    this.songService.filterSongsByGenre(this.selectedGenre).subscribe({
      next: (data: any[]) => this.songs = data,
      error: (err: any) => console.error('Filter failed:', err)
    });
  }

  clearFilters() {
    this.searchQuery = '';
    this.selectedGenre = '';
    this.fetchSongs();
  }

  // --- NEW: Play Song updates play count ---
  playSong(song: any) {
    console.log('Playing:', song.title);
    this.currentSong = song;

    this.songService.incrementPlayCount(song.songId).subscribe({
      next: () => {
        song.playCount = (song.playCount || 0) + 1; // Instant UI update
      },
      error: (err: any) => console.error('Failed to update play count:', err)
    });
  }

  getAudioUrl(fileName: string): string {
    return `${environment.apiUrl}/songs/play/${fileName}`;
  }

  getCoverImageUrl(fileName: string | null): string {
    if (!fileName) {
      return 'assets/default-cover.jpg'; 
    }
    return `${environment.apiUrl}/songs/image/${fileName}`;
  }

  onLogout() {
    this.authService.logout();       
    this.router.navigate(['/login']); 
  }
}