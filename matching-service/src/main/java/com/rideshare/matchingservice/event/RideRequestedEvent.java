package com.rideshare.matchingservice.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Event consume from Kafka topic: ride.requested
// Published by Ride Service when a rider request a ride
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
