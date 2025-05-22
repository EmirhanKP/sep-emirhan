package com.example.sepdrivebackend.repository;

import com.example.sepdrivebackend.model.RideRequest;
import com.example.sepdrivebackend.model.RideStatus;
import com.example.sepdrivebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {

    // Methode, um eine aktive Anfrage für einen bestimmten Kunden zu finden
    // Wird benötigt, um die Regel "nur eine aktive Anfrage pro Kunde" zu prüfen.
    Optional<RideRequest> findByCustomerAndStatus(User customer, RideStatus status);

}
