// src/app/components/profile/profile.component.ts

import { Component, OnInit } from '@angular/core';
// CommonModule für *ngIf etc., DatePipe/DecimalPipe falls im Template genutzt
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
// ReactiveFormsModule etc. beibehalten, falls andere Formulare existieren
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import {tap, catchError, switchMap} from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../services/user.service';
import { UserProfile } from '../../models/user-profile.model';
import {map} from 'leaflet';
import {ActivatedRoute, ParamMap } from '@angular/router';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
    // DatePipe, // Einkommentieren falls benötigt
    // DecimalPipe // Einkommentieren falls benötigt
  ],

  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  userProfile$!: Observable<UserProfile | null>;
  errorMessage: string | null = null;

  // Für Templatezugriffe
  backendUrl = 'http://localhost:8080'; // Basis-URL Backend
  defaultProfilePic = 'assets/default-profile.png'; // Pfad zum Default-Bild (Frontend-Asset)
  // Wird dieses Formular tatsächlich genutzt? Sonst können fb/FormGroup entfernt werden.
  uploadForm: FormGroup;

  constructor(
    private userService: UserService,
    private http: HttpClient,
    private fb: FormBuilder,
    private route: ActivatedRoute
  ) {
    this.uploadForm = this.fb.group({
      profilePicture: [null]
    });
  }

  currentUser: any;
  ngOnInit(): void {
    this.loadProfile();
    this.userService.getMyProfile().subscribe(user => {
      this.currentUser = user;
    });
  }

  private loadProfile(): void {
    this.errorMessage = null;
    this.userProfile$ = this.userService.getMyProfile().pipe(
      tap(profile => {
        // Optional: localStorage Logik - nutzt jetzt die neue URL-Funktion
      }),
      catchError(err => {
        console.error('Error loading profile:', err);
        this.errorMessage = err.error?.message || err.message || 'Profil konnte nicht geladen werden.';
        return of(null);
      })
    );
  }


  /**
   * Gibt die korrekte Bild-URL für das Template zurück.
   */
  getProfilePicUrl(profile: UserProfile | null): string {
    console.log('Profilbild-URL:', profile?.profilePictureUrl);
    return profile?.profilePictureUrl || this.defaultProfilePic;
  }

  /**
   * Wird aufgerufen, wenn der Benutzer eine neue Datei auswählt.
   * Validiert, lädt hoch und löst das Neuladen des Profils aus.
   */
  onFileSelected(event: Event): void {
    this.errorMessage = null;
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) {
      input.value = ''; // Zurücksetzen falls Auswahl abgebrochen
      return;
    }

    const file = input.files[0];
    console.log('FormData wird gesendet mit Datei:', file.name, file.type, file.size);
    // Typ-Check (jpeg UND jpg erlauben)
    if (!/^image\/(png|jpeg|jpg|gif)$/.test(file.type)) {
      this.errorMessage = 'Nur PNG, JPEG, JPG oder GIF Dateien erlaubt.';
      input.value = '';
      return;
    }

    // Größen-Check (Beispiel: max 5MB)
    const maxSizeInBytes = 5 * 1024 * 1024; // 5 MB
    if (file.size > maxSizeInBytes) {
      this.errorMessage = `Datei ist zu groß (max. ${maxSizeInBytes / 1024 / 1024} MB erlaubt).`;
      input.value = '';
      return;
    }

    const formData = new FormData();
    // Der Key 'profilePicture' muss zum @RequestParam im Backend-Controller passen
    formData.append('profileImage', file, file.name);

    const userId = this.currentUser.id;


    input.value = ''; // Input leeren, damit gleiche Datei nochmal gewählt werden kann

    // Upload starten

    this.userService.uploadProfilePicture(userId, formData).subscribe({
      next: (response) => { // Idealerweise gibt Backend hier direkt neues Profil/URL zurück
        console.log('Upload erfolgreich, lade Profil neu...');
        this.errorMessage = null;

        this.loadProfile(); // Lädt Profil neu -> getProfilePicUrl wird neu aufgerufen
      },
      error: err => {
        console.error('Upload fehlgeschlagen:', err);
        this.errorMessage = err.error?.message || err.message || 'Profilbild konnte nicht hochgeladen werden.';
      }
    });
  }
  /**
   * Setzt das Profilbild wieder aufs Standard zurück
   */
  resetProfilePicture(): void {
    this.userProfile$.subscribe(profile => {
      if (!profile) return;

      this.http.post(`/api/users/${profile.id}/reset-profileImage`, {}).subscribe({
        next: () => {
          // Lade das Profil neu, damit das Standardbild angezeigt wird
          this.userProfile$ = this.userService.getMyProfile();
        },
        error: err => {
          console.error('Fehler beim Zurücksetzen des Profilbilds:', err);
        }
      });
    });
  }
}
