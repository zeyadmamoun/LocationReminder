package com.example.android.locationreminder.locationreminders.geofence

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest

class GeofenceHandler {

    fun createGeofence(id: String,latitude: Double,longitude: Double): Geofence {
        return Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId(id)

            // Set the circular region of this geofence.
            .setCircularRegion(
                latitude,
                longitude,
                100F
            )
            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun getGeofencingRequest(geofenceList: MutableList<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }
}