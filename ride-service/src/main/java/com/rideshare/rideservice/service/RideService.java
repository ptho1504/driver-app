package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.RideRequest;
import com.rideshare.rideservice.dto.RideResponse;
import com.rideshare.rideservice.event.RideRequestedEvent;
import com.rideshare.rideservice.model.Ride;
import com.rideshare.rideservice.model.RideStatus;
import com.rideshare.rideservice.repository.RideRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {

    private static final String RIDE_REQUESTED_TOPIC = "ride.requested";
    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideRequestedEvent> kafkaTemplate;

    public @Nullable RideResponse requestRide(@Valid RideRequest rideRequest) {
        log.info("Ride requested from rider: {}", rideRequest.getRiderId());

        // Save ride to database
        Ride ride = new Ride();
        ride.setRiderId(rideRequest.getRiderId());
        ride.setPickupAddress(rideRequest.getPickupAddress());
        ride.setPickupLatitude(rideRequest.getPickupLatitude());
        ride.setPickupLongitude(rideRequest.getPickupLongitude());
        ride.setDropAddress(rideRequest.getDropAddress());
        ride.setDropLatitude(rideRequest.getDropLatitude());
        ride.setDropLongitude(rideRequest.getDropLongitude());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setEstimateFare(calculateEstimateFare(rideRequest));

        Ride savedRide = rideRepository.save(ride);

        RideRequestedEvent rideRequestedEvent = new RideRequestedEvent(
                savedRide.getId(),
                savedRide.getRiderId(),
                savedRide.getPickupAddress(),
                savedRide.getPickupLatitude(),
                savedRide.getPickupLongitude(),
                savedRide.getDropAddress(),
                savedRide.getDropLatitude(),
                savedRide.getDropLongitude()
        );

        kafkaTemplate.send(RIDE_REQUESTED_TOPIC, savedRide.getId(), rideRequestedEvent);

        log.info("RideRequestEvent published to Kafka  from rider: {}", savedRide.getId());

        savedRide.setStatus(RideStatus.MATCHING);
        return mapToResponse(rideRepository.save(savedRide));
    }

    public RideResponse getRideById(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() ->  new RuntimeException("Ride not found"));
        return mapToResponse(ride);
    }

    public List<RideResponse> getRidesByRider(String rideId) {
        return rideRepository.findByRiderIdOrderByCreatedAtDesc(rideId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public RideResponse startRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() ->  new RuntimeException("Ride not found"));

        if(ride.getStatus() != RideStatus.ACCEPTED) {
            throw new RuntimeException("Ride is not accepted. Ride should be accepted");

        }

        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());

        return mapToResponse(rideRepository.save(ride));
    }

    public RideResponse completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() ->  new RuntimeException("Ride not found"));

        if(ride.getStatus() != RideStatus.RIDE_STARTED) {
            throw new RuntimeException("Ride is not ride started. Ride should be ride started");

        }

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        ride.setEstimateFare(ride.getEstimateFare());

        return mapToResponse(rideRepository.save(ride));
    }

    public RideResponse cancelRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() ->  new RuntimeException("Ride not found"));
        ride.setStatus(RideStatus.CANCELLED);
        return mapToResponse(rideRepository.save(ride));
    }

    private double calculateEstimateFare(RideRequest rideRequest) {
        double lat1 = Math.toRadians(rideRequest.getPickupLatitude());
        double lat2 = Math.toRadians(rideRequest.getDropLatitude());

        double lon1 = Math.toRadians(rideRequest.getPickupLongitude());
        double lon2 = Math.toRadians(rideRequest.getDropLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double R = 6371;
        double distance = R * c;

        double baseFare = 2.0;
        double pricePerKm = 1.5;

        return baseFare + (distance * pricePerKm);
    }

    public void updateRideWithDriver(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() ->  new RuntimeException("Ride not found"));

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }


    private RideResponse mapToResponse(Ride ride) {
        RideResponse response = new RideResponse();
        response.setId(ride.getId());
        response.setRiderId(ride.getRiderId());
        response.setDriverId(ride.getDriverId());
        response.setPickupAddress(ride.getPickupAddress());
        response.setPickupLatitude(ride.getPickupLatitude());
        response.setPickupLongitude(ride.getPickupLongitude());
        response.setDropAddress(ride.getDropAddress());
        response.setDropLatitude(ride.getDropLatitude());
        response.setDropLongitude(ride.getDropLongitude());
        response.setStatus(ride.getStatus());
        response.setEstimateFare(ride.getEstimateFare());
        response.setActualFare(ride.getActualFare());
        response.setCompletedAt(ride.getCompletedAt());
        response.setCreatedAt(ride.getCreatedAt());
        response.setUpdatedAt(ride.getUpdatedAt());
        response.setStartedAt(ride.getStartedAt());
        return response;
    }
}

