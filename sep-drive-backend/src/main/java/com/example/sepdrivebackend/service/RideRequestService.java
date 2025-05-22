package com.example.sepdrivebackend.service;
import com.example.sepdrivebackend.dto.CreateRideRequestDto;
import com.example.sepdrivebackend.dto.RideRequestDto;
import com.example.sepdrivebackend.model.RideRequest;
import com.example.sepdrivebackend.model.RideStatus;
import com.example.sepdrivebackend.model.User;
import com.example.sepdrivebackend.repository.RideRequestRepository;
import com.example.sepdrivebackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service // Markiert diese Klasse als Spring Service Bean
@RequiredArgsConstructor // Lombok generiert automatisch einen Konstruktor für finale Felder
public class RideRequestService {

    private final RideRequestRepository rideRequestRepository;
    private final UserRepository userRepository; // Wird benötigt, um den Kunden zu finden

    /**
     * Ruft die aktuell aktive Fahranfrage für einen Kunden ab.
     * @param customerUsername Der Username des Kunden.
     * @return Ein Optional, das das RideRequestDto enthält, wenn eine aktive Anfrage existiert, sonst leer.
     */
    @Transactional(readOnly = true) // Keine Datenänderung, nur Lesen
    public Optional<RideRequestDto> getActiveRideRequest(String customerUsername) {
        // Finde den User (Kunde) anhand des Usernamens
        User customer = userRepository.findByUsername(customerUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + customerUsername));

        // Finde die aktive Anfrage für diesen Kunden
        return rideRequestRepository.findByCustomerAndStatus(customer, RideStatus.ACTIVE)
                .map(RideRequestDto::fromEntity); // Konvertiere das gefundene Entity (falls vorhanden) in ein DTO
    }

    /**
     * Erstellt eine neue Fahranfrage für einen Kunden.
     * @param requestDto Die Daten der neuen Anfrage.
     * @param customerUsername Der Username des anfragenden Kunden.
     * @return Das DTO der neu erstellten Fahranfrage.
     * @throws IllegalStateException wenn der Kunde bereits eine aktive Anfrage hat.
     */
    @Transactional // Daten werden geändert (neue Anfrage erstellt)
    public RideRequestDto createRideRequest(CreateRideRequestDto requestDto, String customerUsername) {
        User customer = userRepository.findByUsername(customerUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + customerUsername));

        // Regel prüfen: Hat der Kunde bereits eine aktive Anfrage?
        Optional<RideRequest> existingActiveRequest = rideRequestRepository.findByCustomerAndStatus(customer, RideStatus.ACTIVE);
        if (existingActiveRequest.isPresent()) {
            // Wenn ja, Fehler werfen
            throw new IllegalStateException("Customer " + customerUsername + " already has an active ride request.");
        }

        // Neue RideRequest-Entität erstellen
        RideRequest newRequest = new RideRequest();
        newRequest.setCustomer(customer);
        newRequest.setStartLatitude(requestDto.getStartLatitude());
        newRequest.setStartLongitude(requestDto.getStartLongitude());
        newRequest.setStartAddress(requestDto.getStartAddress()); // Kann null sein
        newRequest.setDestinationLatitude(requestDto.getDestinationLatitude());
        newRequest.setDestinationLongitude(requestDto.getDestinationLongitude());
        newRequest.setDestinationAddress(requestDto.getDestinationAddress()); // Kann null sein
        newRequest.setRequestedCarClass(requestDto.getRequestedCarClass());
        newRequest.setStatus(RideStatus.ACTIVE); // Status auf AKTIV setzen
        // createdAt und updatedAt werden automatisch durch @CreationTimestamp/@UpdateTimestamp gesetzt

        // Die neue Anfrage speichern
        RideRequest savedRequest = rideRequestRepository.save(newRequest);

        // Das gespeicherte Entity als DTO zurückgeben
        return RideRequestDto.fromEntity(savedRequest);
    }

    /**
     * Storniert die aktuell aktive Fahranfrage eines Kunden.
     * @param customerUsername Der Username des Kunden.
     * @throws EntityNotFoundException wenn der Kunde keine aktive Anfrage hat.
     */
    @Transactional // Daten werden geändert (Status wird aktualisiert)
    public void cancelActiveRideRequest(String customerUsername) {
        User customer = userRepository.findByUsername(customerUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + customerUsername));

        // Finde die aktive Anfrage
        RideRequest activeRequest = rideRequestRepository.findByCustomerAndStatus(customer, RideStatus.ACTIVE)
                // Wenn keine aktive Anfrage gefunden wird, Fehler werfen
                .orElseThrow(() -> new EntityNotFoundException("No active ride request found for customer: " + customerUsername));

        // Status auf CANCELLED setzen
        activeRequest.setStatus(RideStatus.CANCELLED);
        // updatedAt wird automatisch durch @UpdateTimestamp aktualisiert

        // Die Änderung speichern (technisch nicht immer nötig bei gemanagten Entities, aber sicher ist sicher)
        rideRequestRepository.save(activeRequest);
    }
}