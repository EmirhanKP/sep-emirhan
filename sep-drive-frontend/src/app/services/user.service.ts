import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { UserProfile } from '../models/user-profile.model'; // Pfad prüfen!
import { Observable, BehaviorSubject, tap, catchError, of } from 'rxjs'; // BehaviorSubject, tap, catchError, of importieren

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  // BehaviorSubject, um das aktuelle UserProfile zu halten und zu verteilen
  // Initialwert ist null, bis das Profil geladen wurde
  private currentUserProfileSubject = new BehaviorSubject<UserProfile | null>(null);
  public currentUserProfile$ = this.currentUserProfileSubject.asObservable(); // Als Observable für andere Komponenten

  constructor(private http: HttpClient) { }

  /**
   * Ruft das Profil des aktuell eingeloggten Users ab und aktualisiert den currentUserProfileSubject.
   */
  getMyProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/me`).pipe(
      tap(profile => {
        this.currentUserProfileSubject.next(profile); // BehaviorSubject mit dem neuen Profil aktualisieren
        // Optional: localStorage aktualisieren, falls für direkten Zugriff genutzt
        if (profile && profile.profilePictureUrl) {
          localStorage.setItem('profileImageUrl', profile.profilePictureUrl);
        } else {
          localStorage.removeItem('profileImageUrl');
        }
      }),
      catchError(err => {
        console.error('Fehler beim Laden des Profils in UserService:', err);
        this.currentUserProfileSubject.next(null); // Bei Fehler auf null setzen
        localStorage.removeItem('profileImageUrl');
        return of(err); // Fehler weitergeben oder behandeln
      })
    );
  }

  // Methode, um das Profil initial zu laden (z.B. nach Login)
  public loadInitialProfile(): void {
    if (localStorage.getItem('authToken')) { // Nur laden, wenn ein Token existiert
      this.getMyProfile().subscribe({
        error: err => console.error('Fehler beim initialen Laden des Profils:', err)
      });
    }
  }

  // Gibt das aktuell im BehaviorSubject gespeicherte Profil zurück (kann null sein)
  public getCurrentUserProfile(): UserProfile | null {
    return this.currentUserProfileSubject.getValue();
  }

  getUserProfile(username: any): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/${username}`);
  }

  uploadProfilePicture(userId: number, formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/${userId}/upload-profileImage`, formData, {
      responseType: 'text' // Annahme: Backend gibt String (z.B. neue URL oder Erfolgsmeldung) zurück
    });
    // Das Neuladen des Profils (getMyProfile) wird in der ProfileComponent nach dem Upload ausgelöst
  }

  // Methode, um das Profil im Service zurückzusetzen (z.B. beim Logout)
  clearUserProfile(): void {
    this.currentUserProfileSubject.next(null);
    localStorage.removeItem('profileImageUrl'); // Auch aus localStorage entfernen
  }
  searchUsers(query: string): Observable<UserProfile[]> {
    const params = new HttpParams().set('q', query);
    return this.http.get<UserProfile[]>(`${this.apiUrl}/search`, { params });
  }
}
