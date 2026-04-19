package com.rideshare.rideservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {
    @NotBlank(message = "Rider Id is required")
    private String riderId;

    @NotNull(message = "pickupLatitude is required")
    private double pickupLatitude;

    @NotNull(message = "pickupLongitude is required")
    private double pickupLongitude;

    @NotBlank(message = "pickupAddress is required")
    private String pickupAddress;

    @NotNull(message = "dropLatitude is required")
    private double dropLatitude;

    @NotNull(message = "dropLongitude is required")
    private double dropLongitude;

    @NotBlank(message = "dropAddress is required")
    private String dropAddress;

}
