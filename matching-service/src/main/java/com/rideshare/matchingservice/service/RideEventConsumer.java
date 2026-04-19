package com.rideshare.matchingservice.service;
import com.rideshare.matchingservice.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {
    private final MatchingService matchingService;

    // Listen to ride.requested kafka topic
    // Trigger every time Ride Service published a new ride request
    // Flow
    // Ride Service -> Kafka (ride.requested) -> This consumer -> Matching service

    @KafkaListener(
            topics = "ride.requested",
            groupId = "matching-service-group"
    )
    public void consumeRideRequest(RideRequestedEvent rideRequest) {
        try {
            matchingService.matchDriverForRide(rideRequest);
        }catch (Exception e) {
            log.error("Error processing ride request: {} - {}", rideRequest.getRideId(), e.getMessage());
        }
    }
}
