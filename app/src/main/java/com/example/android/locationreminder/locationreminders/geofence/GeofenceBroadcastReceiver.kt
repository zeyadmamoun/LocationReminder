package com.example.android.locationreminder.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.android.locationreminder.authentication.AuthenticationActivity.Companion.TAG
import com.example.android.locationreminder.locationreminders.geofence.GeofenceTransitionsJobIntentService.Companion.enqueueWork
import com.example.android.locationreminder.utils.sendNotification
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//TODO: implement the onReceive method to receive the geofencing events at the background
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        val geofencingTransition = geofencingEvent.geofenceTransition
         if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
             geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
                enqueueWork(context,intent)
         }
    }

}