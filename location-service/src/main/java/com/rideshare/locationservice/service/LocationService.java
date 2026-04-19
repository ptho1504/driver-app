package com.rideshare.locationservice.service;

import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.NearByResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {
    private static final String DRIVERS_GEO_KEY = "drivers";
    private final RedisTemplate<String, String> redisTemplate;

    // Update driver location in Redis
    // Call every 3 seconds
    // Maps to Redis GEOADD commands
    public void updateLocation(DriverLocationRequest request) {
        log.info("Updating location for driver {}", request.getDriverId());
        // Longitude First
        //  Latitude Second
        Point driverPoint = new Point(
                request.getLongitude(),
                request.getLatitude());

        redisTemplate.opsForGeo().add(
                DRIVERS_GEO_KEY,
                driverPoint,
                request.getDriverId()
        );
        log.info("Updated location for driver {}", request.getDriverId());
    }

    // Find nearby drivers with in given radius
    // Called by Matching Service on ride request
    // Map to Redis FEORADIUS command
    public List<NearByResponse>
        findNearByDrivers(double latitude, double longitude, double radius) {
        log.info("Finding near by drivers lat: {}, long: {}, radius: {}", latitude, longitude, radius);

        Circle searchArea = new Circle(
                new Point(longitude, latitude),
                new Distance(radius, Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo()
                .radius(DRIVERS_GEO_KEY,
                        searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeCoordinates()
                                .includeDistance()
                                .sortAscending()
                                .limit(10));

        List<NearByResponse> nearByDrivers = new ArrayList<>();

        if (results != null) {
            results.getContent().forEach(result -> {
                RedisGeoCommands.GeoLocation<String> location = result.getContent();
                nearByDrivers.add(new NearByResponse(
                        location.getName(),
                        location.getPoint().getY(),
                        location.getPoint().getX(),
                        result.getDistance().getValue()
                ));
            });
        }

        log.info("Found {} near by drivers", nearByDrivers.size());

        return nearByDrivers;
    }

    // Remove driverId
    // Map to Redis ZREM command
    public void removeDriver(String driverId) {
        log.info("Removing driver {}", driverId);
        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY, driverId);
    }
}
