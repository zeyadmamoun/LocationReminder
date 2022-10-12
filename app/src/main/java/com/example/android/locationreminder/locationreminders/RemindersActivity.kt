package com.example.android.locationreminder.locationreminders

import android.Manifest
import android.annotation.TargetApi
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.android.locationreminder.R
import com.example.android.locationreminder.authentication.AuthenticationActivity.Companion.TAG
import com.example.android.locationreminder.databinding.ActivityRemindersBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar

private const val REQUEST_TURN_DEVICE_LOCATION_ON = 3

class RemindersActivity : AppCompatActivity() {

    private val runningQOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<String>
    private lateinit var binding: ActivityRemindersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemindersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if(isGranted){
                Log.i(TAG,"permissions are granted")
                requestForegroundAndBackgroundLocationPermissions()
            }else{
                Snackbar.make(this,binding.layout,"permissions should be granted to save reminders",Snackbar.LENGTH_LONG)
                    .setAction("Accept"){
                        requestForegroundAndBackgroundLocationPermissions()
                    }
                    .show()
            }
        }

        requestForegroundAndBackgroundLocationPermissions()
        checkDeviceLocationSettings()
    }

    private fun foregroundLocationPermissionApproved(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(29)
    private fun backgroundLocationPermissionApproved(): Boolean{
        return if (runningQOrLater) {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }else{
            true
        }
    }

    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (!foregroundLocationPermissionApproved()){
            requestPermissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!backgroundLocationPermissionApproved()){
            requestPermissionsLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun checkDeviceLocationSettings(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try {
                    exception.startResolutionForResult(this, REQUEST_TURN_DEVICE_LOCATION_ON)
                }catch (sendEx: IntentSender.SendIntentException){
                    Log.d(TAG,"Error getting location settings resolution: " + sendEx.message)
                }
            }else{
                Snackbar.make(
                    binding.layout,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
