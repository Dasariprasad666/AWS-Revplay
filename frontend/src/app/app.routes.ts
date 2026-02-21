import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { Home } from './features/home/home';
import { UploadSong } from './features/music/upload-song/upload-song'; 
import { Favorites } from './features/favorites/favorites'; 
import { MyMusic } from './features/music/my-music/my-music'; 
import { Playlists } from './features/playlists/playlists/playlists';
import { PlaylistDetail } from './features/playlists/playlist-detail/playlist-detail'; // 🌟 NEW: Import Playlist Detail

import { authGuard } from './core/guards/auth/auth-guard';  
import { artistGuard } from './core/guards/artist/artist-guard';  

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  
  // Protected by authGuard (must be logged in)
  { path: 'home', component: Home, canActivate: [authGuard] }, 
  { path: 'favorites', component: Favorites, canActivate: [authGuard] }, 
  { path: 'playlists', component: Playlists, canActivate: [authGuard] },
  
  //  NEW: Dynamic route for looking inside a specific playlist
  { path: 'playlists/:id', component: PlaylistDetail, canActivate: [authGuard] }, 
  
  // Protected by artistGuard (must be logged in AND an ARTIST)
  { path: 'upload', component: UploadSong, canActivate: [artistGuard] },
  { path: 'my-music', component: MyMusic, canActivate: [artistGuard] },
  
  { path: '', redirectTo: '/login', pathMatch: 'full' } 
];