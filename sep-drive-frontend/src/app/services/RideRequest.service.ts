import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, of, throwError } from 'rxjs';
import { RideRequestDto } from '../models/ride-request-dto.model';       // Import Response-DTO
import {CreateRideRequestDto} from '../models/create-ride-request-dto.model';

@Injectable({
  providedIn: 'root'
})
export class RideRequestService {

  // Basis-URL für die Fahranfrage-Endpunkte im Backend
  private apiUrl = 'http://localhost:8080/api/ride-requests';

  constructor(private http: HttpClient) { }

  /**
   * Sendet eine Anfrage zum Erstellen einer neuen Fahranfrage an das Backend.
   * @param data Die Daten für die neue Anfrage (Start, Ziel, Klasse).
   * @returns Ein Observable, das das DTO der erstellten Anfrage liefert.
   */
  createRequest(data: CreateRideRequestDto): Observable<RideRequestDto> {
    // Sendet POST an /api/ride-requests
    // Der AuthInterceptor sollte das JWT hinzufügen.
    return this.http.post<RideRequestDto>(this.apiUrl, data);
    // Die Fehlerbehandlung (z.B. für 409 Conflict) sollte in der Komponente erfolgen,
    // die diese Methode aufruft.
  }

  /**
   * Ruft die aktuell aktive Fahranfrage des eingeloggten Benutzers ab.
   * @returns Ein Observable, das das RideRequestDto liefert, falls eine aktive Anfrage existiert, sonst null.
   */
  getActiveRequest(): Observable<RideRequestDto | null> {
    // Sendet GET an /api/ride-requests/active
    return this.http.get<RideRequestDto>(`${this.apiUrl}/active`).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          // Nur als Info, keine aktive Anfrage gefunden
          return of(null); // Gibt null zurück, damit die Komponente weiß, dass nichts da ist
        } else {
          // Andere Fehler (401, 500 etc.) an die Komponente weitergeben.
          console.error('Error fetching active ride request:', error);
          return throwError(() => error); // Wirft den Fehler weiter
        }
      })
    );
  }

  /**
   * Sendet eine Anfrage zum Stornieren/Löschen der aktiven Fahranfrage.
   * @returns Ein Observable<void>, das bei Erfolg abschließt.
   */
  cancelActiveRequest(): Observable<void> {
    // Sendet DELETE an /api/ride-requests/active
    return this.http.delete<void>(`${this.apiUrl}/active`);
    // Fehlerbehandlung (z.B. 404 wenn doch keine Anfrage da war) sollte in der Komponente erfolgen.
  }
}
