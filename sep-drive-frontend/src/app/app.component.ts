import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, RouterLink} from '@angular/router'; // RouterLink entfernt
import { AuthService } from './services/auth.service';
import { UserService } from './services/user.service';

//import { UserProfile } from './models/user-profile.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet

  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  currentYear = new Date().getFullYear();
  isMenuOpen = false;

  backendUrl = 'http://localhost:8080';
  defaultProfilePic = 'assets/default-profile.png';
  profileImageUrl: string = this.defaultProfilePic;

  constructor(
    public authService: AuthService,
    public router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      //this.loadProfileImage();
    }
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  logout(): void {
    this.authService.logout();
    this.isMenuOpen = false;
    this.profileImageUrl = this.defaultProfilePic;
  }

  getProfileImageUrl(): string {
    return this.profileImageUrl;
  }
  onLogoClick(): void {
    console.log('Logo geklickt!');
    // Beispiel: Wenn das Seitenmenü geöffnet ist, schließe es
    if (this.isMenuOpen) {
      this.toggleMenu(); // Deine bestehende Methode zum Schließen des Menüs
    }
}
}
