package com.rideshare.rideservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestedEvent {
    private String rideId;
    private String riderId;

    private String pickupAddress;
    private double pickupLatitude;
    private double pickupLongitude;

    private String dropAddress;
    private double dropLatitude;
    private double dropLongitude;
}
