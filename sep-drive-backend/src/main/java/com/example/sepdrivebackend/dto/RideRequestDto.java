package com.example.sepdrivebackend.dto;


import com.example.sepdrivebackend.model.RideRequest;
import com.example.sepdrivebackend.model.RideStatus;
import com.example.sepdrivebackend.model.User;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data // Lombok für Getter/Setter etc.
@NoArgsConstructor // Lombok für leeren Konstruktor
public class RideRequestDto {
    private Long id;
    private Long customerId;
    private String customerUsername;
    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private String startAddress;
    private BigDecimal destinationLatitude;
    private BigDecimal destinationLongitude;
    private String destinationAddress;
    private User.CarClass requestedCarClass;
    private RideStatus status;
    private Instant createdAt; // Sende Instant als ISO-String

    // Statische Methode, um ein DTO aus einem Entity zu erstellen
    public static RideRequestDto fromEntity(RideRequest rideRequest) {
        RideRequestDto dto = new RideRequestDto();
        dto.setId(rideRequest.getId());
        if (rideRequest.getCustomer() != null) { // Sicherstellen, dass Customer geladen ist
            dto.setCustomerId(rideRequest.getCustomer().getId());
            dto.setCustomerUsername(rideRequest.getCustomer().getUsername());
        }
        dto.setStartLatitude(rideRequest.getStartLatitude());
        dto.setStartLongitude(rideRequest.getStartLongitude());
        dto.setStartAddress(rideRequest.getStartAddress());
        dto.setDestinationLatitude(rideRequest.getDestinationLatitude());
        dto.setDestinationLongitude(rideRequest.getDestinationLongitude());
        dto.setDestinationAddress(rideRequest.getDestinationAddress());
        dto.setRequestedCarClass(rideRequest.getRequestedCarClass());
        dto.setStatus(rideRequest.getStatus());
        dto.setCreatedAt(rideRequest.getCreatedAt());
        return dto;
    }
}