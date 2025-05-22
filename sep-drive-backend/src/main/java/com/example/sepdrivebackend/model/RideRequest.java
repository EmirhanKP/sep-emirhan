package com.example.sepdrivebackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ride_requests")
@Getter
@Setter
@NoArgsConstructor
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Beziehung zum Kunden (User), der die Fahrt angefragt hat
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY) // Lazy Loading
    @JoinColumn(name = "customer_id", nullable = false) // Spaltenname in dieser Tabelle
    private User customer;

    // Startpunkt
    @NotNull
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal startLatitude;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal startLongitude;

    @Column(length = 255) // Adresse optional, Länge begrenzen
    private String startAddress;

    // Zielpunkt
    @NotNull
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal destinationLatitude;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal destinationLongitude;

    @Column(length = 255)
    private String destinationAddress;

    // Gewünschte Fahrzeugklasse (aus dem User Enum)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private User.CarClass requestedCarClass;

    // Aktueller Status der Anfrage
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;


    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}
