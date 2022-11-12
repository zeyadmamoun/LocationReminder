package com.example.android.locationreminder.locationreminders.savereminder

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.android.locationreminder.R
import com.example.android.locationreminder.base.BaseFragment
import com.example.android.locationreminder.base.NavigationCommand
import com.example.android.locationreminder.databinding.FragmentSaveReminderBinding
import com.example.android.locationreminder.locationreminders.RemindersActivity
import com.example.android.locationreminder.locationreminders.geofence.GeofenceBroadcastReceiver
import com.example.android.locationreminder.locationreminders.geofence.GeofenceHandler
import com.example.android.locationreminder.locationreminders.reminderslist.ReminderDataItem
import com.example.android.locationreminder.locationreminders.savereminder.selectreminderlocation.REQUEST_LOCATION_PERMISSION
import com.example.android.locationreminder.utils.setDisplayHomeAsUpEnabled
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject
import java.util.*

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHandler: GeofenceHandler
//    private lateinit var reminderActivity: RemindersActivity

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireActivity(), GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        reminderActivity = activity as RemindersActivity

        geofencingClient = LocationServices.getGeofencingClient(requireContext())
        geofenceHandler = GeofenceHandler()
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.addReminderBtn.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value
            val geofenceId = UUID.randomUUID().toString()



//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
            val geofenceList = mutableListOf<Geofence>()
            geofenceList.add(geofenceHandler.createGeofence(geofenceId,latitude!!,longitude!!))
            val geofencingRequest = geofenceHandler.getGeofencingRequest(geofenceList)

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }

            geofencingClient.addGeofences(geofencingRequest,geofencePendingIntent)?.run {
                addOnSuccessListener {
                    Toast.makeText(requireActivity(),"$title geofence is created",Toast.LENGTH_SHORT).show()
                    //  2) save the reminder to the local db
                    val reminder = ReminderDataItem(title,description,location,latitude,longitude,geofenceId)
                    _viewModel.validateAndSaveReminder(reminder)

                    _viewModel.navigationCommand.value = NavigationCommand.Back
                }
                addOnFailureListener {
                    // Failed to add geofence
                    Toast.makeText(requireActivity(),"$title geofence is failed",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }
}
