package com.rideshare.locationservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByResponse {
    private String driverId;
    private double latitude;
    private double longitude;
    private double distanceInKm;
}
