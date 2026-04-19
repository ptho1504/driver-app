package com.rideshare.rideservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {
    @NotBlank(message = "Rider Id is required")
    private String riderId;

    @NotBlank(message = "pickupLatitude is required")
    private double pickupLatitude;

    @NotBlank(message = "pickupLongitude is required")
    private double pickupLongitude;

    @NotBlank(message = "pickupAddress is required")
    private String pickupAddress;

    @NotBlank(message = "dropLatitude is required")
    private double dropLatitude;

    @NotBlank(message = "dropLongitude is required")
    private double dropLongitude;

    @NotBlank(message = "dropAddress is required")
    private String dropAddress;

}
