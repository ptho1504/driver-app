package com.rideshare.locationservice.controller;

import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.NearByResponse;
import com.rideshare.locationservice.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;


    // Call for updating location of drivers each 3 seconds
    @PostMapping("/driver")
    public ResponseEntity<String> updateLocation(
            @RequestBody DriverLocationRequest request) {
        locationService.updateLocation(request);
        return ResponseEntity.ok("Update successfully");
    }

    // Matching service call this when rides is requested
    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByResponse>> getNearByDrivers(
        @RequestParam double latitude,
        @RequestParam double longitude,
        @RequestParam (defaultValue = "5.0") double radius
    ) {
        return ResponseEntity.ok(locationService.findNearByDrivers(latitude, longitude, radius));
    }

    // Called when driver go offline
    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> removeDriver(@PathVariable String driverId) {
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Remove successfully");
    }
}
