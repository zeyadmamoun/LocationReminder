package com.example.android.locationreminder.locationreminders.geofence

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class GeofenceWork(appContext: Context,workerParameters: WorkerParameters)
    :Worker(appContext,workerParameters) {
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}