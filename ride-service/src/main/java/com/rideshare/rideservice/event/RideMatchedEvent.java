package com.rideshare.rideservice.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

// Event published to Kafka topic: ride.matched
// Consume by Ride Service to Update ride with assign driver
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RideMatchedEvent {
    private String rideId;
    private String riderId;
    private String driverId;
    private double driverLatitude;
    private double driverLongitude;
    private double distanceToPickupKm;
}
