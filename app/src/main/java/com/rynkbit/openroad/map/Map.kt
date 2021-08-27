package com.rynkbit.openroad.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.rynkbit.openroad.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay


class Map : Fragment() {
    companion object {
        val TAG = Map::class.java.simpleName
    }

    private lateinit var viewModel: MapViewModel
    private lateinit var map: MapView
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var marker: Marker
    private lateinit var compass: Compass

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]

        map = view.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        map.setMultiTouchControls(true);
        map.controller.setZoom(10.0)
        map.controller.setCenter(GeoPoint(48.8583, 2.2944))

        marker = Marker(map)
        map.overlays.add(marker)

        compass = Compass(requireContext())
        compass.listeners.add(Compass.CompassListener {
            marker.rotation = it
            map.invalidate()
        })

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val point = GeoPoint(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude
                )

                marker.position = point
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                marker.icon = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_expand_less_24,
                    requireActivity().theme
                )

                map.controller.setZoom(19.0)
                map.controller.setCenter(point)
                map.invalidate()
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
            }
        }

        locationRequest = LocationRequest.create()
        locationRequest.interval = 500
        locationRequest.fastestInterval = 100
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        askRequiredPermissions(getRequiredPermissions())
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        compass.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        compass.onPause()
    }


    private fun setInitialLocation() {
        val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "setInitialLocation: ${e.message}", e)
        }
//        try {
//            val fusedLocationClient =
//                LocationServices.getFusedLocationProviderClient(requireContext())
//            fusedLocationClient.requestLocationUpdates(
//                LocationRequest.create(),
//                object : LocationCallback() {
//                    override fun onLocationResult(locationResult: LocationResult) {
//                        super.onLocationResult(locationResult)
//
//                        val point = GeoPoint(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
//                        val marker = Marker(map)
//
//
//                        marker.position = point
//                        marker.rotation = locationResult.lastLocation.bearing
//                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
//                        marker.icon = ResourcesCompat.getDrawable(
//                            resources,
//                            R.drawable.ic_baseline_expand_less_24,
//                            requireActivity().theme
//                        )
//
//                        map.overlays.add(marker)
//                        map.controller.setZoom(19.0)
//                        map.controller.setCenter(point)
//                        map.postInvalidate()
//                        map.invalidate()
//                    }
//                },
//                Looper.getMainLooper()
//            )
//
//        } catch (e: SecurityException) {
//            Log.e(Map::class.java.simpleName, e.message ?: "")
//        }
    }

    private fun askRequiredPermissions(requiredPermissions: List<String>) {
        if (requiredPermissions.isNotEmpty()) {
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    if (isGranted) {
                        setInitialLocation()
                    }
                }

            for (permission in requiredPermissions) {
                requestPermissionLauncher.launch(permission)
            }
        } else {
            setInitialLocation()
        }
    }

    private fun getRequiredPermissions(): List<String> {
        val context = requireContext()
        val permissions = mutableListOf<String>()

        if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        return permissions
    }
}
