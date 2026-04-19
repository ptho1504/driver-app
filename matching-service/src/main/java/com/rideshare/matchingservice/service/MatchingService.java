package com.rideshare.matchingservice.service;

import com.rideshare.matchingservice.client.LocationServiceClient;
import com.rideshare.matchingservice.dto.NearByDriverResponse;
import com.rideshare.matchingservice.event.RideMatchedEvent;
import com.rideshare.matchingservice.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {
    private final LocationServiceClient client;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    private static final String RIDE_MATCHED_TOPIC = "ride.matched";
    private static final double DEFAULT_SEARCH_RADIUS_KM = 5.0;

    // Main matching algorithm
    // Called when RideRequestedEvent is consumed from Kafka
    // STEP
    // 1 Ask Location Service for nearby drivers
    // 2 Score each driver and pick the best one
    // 3 Publish RideMatchedEvent to Kafka
    public void matchDriverForRide(RideRequestedEvent rideRequestedEvent) {
        List<NearByDriverResponse> nearByDriverResponses = client.getNearByDrivers(
                rideRequestedEvent.getPickupLatitude(),
                rideRequestedEvent.getPickupLongitude(),
                DEFAULT_SEARCH_RADIUS_KM
        );

        if (nearByDriverResponses.isEmpty()) {
            log.warn("No drivers found near ride: {}", rideRequestedEvent);
            return;
        }

        Optional<NearByDriverResponse> bestDriver = findBestDriver(nearByDriverResponses);

        if (bestDriver.isEmpty()) {
            log.warn("Could not find best driver for ride");
            return;
        }

        NearByDriverResponse assignedDriver = bestDriver.get();

        RideMatchedEvent matchedEvent = new RideMatchedEvent(
                rideRequestedEvent.getRideId(),
                rideRequestedEvent.getRiderId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInKm());

        kafkaTemplate.send(RIDE_MATCHED_TOPIC, rideRequestedEvent.getRideId(), matchedEvent);
        log.info("Ride matched event published");
    }


    // Driver scoring algorithm
    // Distance: 70%
    // Rating: 30%
    // Score = 1/distance * distanceWeight + rating*ratingWeight

    private Optional<NearByDriverResponse> findBestDriver(
            List<NearByDriverResponse> drivers) {
        double distanceWeight = 0.7;
        double ratingWeight = 0.3;

        return drivers.stream()
                .max(Comparator.comparingDouble(driver -> {
                    double distanceScore = 1.0 / (driver.getDistanceInKm() + 0.1);

                    double simulatedRating = 4.0 + Math.random();
                    return (distanceScore * distanceWeight) + ratingWeight * simulatedRating;
                }));
    }
}
