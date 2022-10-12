package com.example.android.locationreminder.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.android.locationreminder.R
import com.example.android.locationreminder.authentication.AuthenticationActivity.Companion.TAG
import com.example.android.locationreminder.base.BaseFragment
import com.example.android.locationreminder.base.NavigationCommand
import com.example.android.locationreminder.databinding.FragmentSelectLocationBinding
import com.example.android.locationreminder.locationreminders.savereminder.SaveReminderViewModel
import com.example.android.locationreminder.utils.setDisplayHomeAsUpEnabled
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import org.koin.android.ext.android.inject

const val REQUEST_LOCATION_PERMISSION = 1

class SelectLocationFragment : BaseFragment() , OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private var pointOfInterest: PointOfInterest? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        TODO: add the map setup implementation
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


//        TODO: call this function after the user confirms on the selected location
        binding.saveBtn.setOnClickListener {
            pointOfInterest?.let {
                _viewModel.selectedPOI.value = it
                _viewModel.reminderSelectedLocationStr.value = it.name
                _viewModel.latitude.value = it.latLng.latitude
                _viewModel.longitude.value = it.latLng.longitude

                _viewModel.navigationCommand.postValue(
                    NavigationCommand.Back
                )
            }
        }

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.normal_map -> {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            R.id.hybrid_map -> {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
            R.id.satellite_map -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
            R.id.terrain_map -> {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setMapStyle(map)
        onPoiSelected()
        enableMyLocation()
    }

    private fun setMapStyle(map: GoogleMap){
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireActivity(),R.raw.map_style)
            )

            if (!success){
                Log.e(TAG,"Style parsing failed")
            }
        }catch (e: Resources.NotFoundException){
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun onPoiSelected(){
        map.setOnPoiClickListener {
            map.clear()
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(it.latLng)
                    .title(it.name)
            )
            poiMarker?.showInfoWindow()
            pointOfInterest = it
            binding.saveBtn.visibility = View.VISIBLE
        }
    }

    private fun enableMyLocation(){
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }else{
            map.isMyLocationEnabled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

}
