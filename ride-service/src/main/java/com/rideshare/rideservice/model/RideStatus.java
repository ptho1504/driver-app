package com.rideshare.rideservice.model;


/*
 * FLOW
 *  REQUESTED -> MATCHING -> ACCEPTED -> DRIVER_ARRiVING
 *            -> RIDE_STARTED -> COMPLETED
 *            -> CANCELLED
 */
public enum RideStatus {
    REQUESTED,
    MATCHING,
    ACCEPTED,
    DRIVER_ARRIVING,
    RIDE_STARTED,
    COMPLETED,
    CANCELLED,
}
