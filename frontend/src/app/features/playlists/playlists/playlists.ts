import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Playlist } from '../../../core/services/playlist/playlist';

@Component({
  selector: 'app-playlists',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './playlists.html', 
  styleUrl: './playlists.css'      
})
export class Playlists implements OnInit {
  private playlistService = inject(Playlist);

  myPlaylists: any[] = [];
  showCreateModal: boolean = false;
  
  //  NEW: Variables to track Edit Mode
  isEditing: boolean = false;
  editingPlaylistId: number | null = null;
  
  // Object to hold data when user types in the form
  newPlaylist = {
    name: '',
    description: '',
    privacy: 'PUBLIC'
  };

  ngOnInit() {
    this.fetchPlaylists();
  }

  fetchPlaylists() {
    this.playlistService.getMyPlaylists().subscribe({
      next: (data: any[]) => {
        this.myPlaylists = data;
      },
      error: (err: any) => console.error('Failed to load playlists:', err)
    });
  }

  // --- Modal Controls ---
  openCreateModal() {
    this.isEditing = false;
    this.editingPlaylistId = null;
    this.newPlaylist = { name: '', description: '', privacy: 'PUBLIC' }; // Clear form
    this.showCreateModal = true;
  }

  //  NEW: Open modal in Edit Mode and pre-fill data
  openEditModal(event: Event, playlist: any) {
    event.stopPropagation(); // Prevent routing to playlist details
    this.isEditing = true;
    this.editingPlaylistId = playlist.playlistId;
    this.newPlaylist = { 
      name: playlist.name, 
      description: playlist.description || '', 
      privacy: playlist.privacy 
    };
    this.showCreateModal = true;
  }

  closeCreateModal() {
    this.showCreateModal = false;
  }

  //  UPDATED: Handle both Create and Update
  savePlaylist() {
    if (!this.newPlaylist.name.trim()) {
      alert("Playlist name cannot be empty!");
      return;
    }

    if (this.isEditing && this.editingPlaylistId) {
      // UPDATE LOGIC
      this.playlistService.updatePlaylist(this.editingPlaylistId, this.newPlaylist).subscribe({
        next: () => {
          this.fetchPlaylists(); 
          this.closeCreateModal(); 
        },
        error: (err: any) => console.error('Failed to update playlist:', err)
      });
    } else {
      // CREATE LOGIC
      this.playlistService.createPlaylist(this.newPlaylist).subscribe({
        next: () => {
          this.fetchPlaylists(); 
          this.closeCreateModal(); 
        },
        error: (err: any) => console.error('Failed to create playlist:', err)
      });
    }
  }

  deletePlaylist(event: Event, playlistId: number) {
    event.stopPropagation(); 
    if (confirm('Are you sure you want to delete this playlist?')) {
      this.playlistService.deletePlaylist(playlistId).subscribe({
        next: () => {
          this.myPlaylists = this.myPlaylists.filter(p => p.playlistId !== playlistId);
        },
        error: (err: any) => console.error('Failed to delete playlist:', err)
      });
    }
  }
}