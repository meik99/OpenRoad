package com.rynkbit.openroad.ui.map

import android.Manifest
import android.content.pm.PackageManager
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
import com.rynkbit.openroad.logic.map.Compass
import kotlinx.android.synthetic.main.map_fragment.*
import org.osmdroid.bonuspack.location.GeocoderMapzen
import org.osmdroid.bonuspack.location.GeocoderNominatim
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors


class MapFragment : Fragment() {
    companion object {
        val TAG = MapFragment::class.java.simpleName
    }

    private lateinit var viewModel: MapViewModel
    private lateinit var mapView: MapView
    private lateinit var compass: Compass
    private lateinit var map: Map
    private lateinit var locationMarker: LocationMarker

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

        mapView = view.findViewById(R.id.map)
        compass = Compass(requireContext())

        initMap()
        Permissions(this).apply {
            listener = Permissions.PermissionsListener { setInitialLocation() }
            askRequiredPermissions(getRequiredPermissions())
        }

        btnCalcRoute.setOnClickListener {
            val begin = editBegin.text.toString()
            val end = editEnd.text.toString()

            Executors.newSingleThreadExecutor().execute {
                try {
                    val geocoder =  GeocoderNominatim(
                        Locale.getDefault(),
                        "OpenRoad"
                    )

                    val beginAddresses = geocoder.getFromLocationName(begin, 10)
                    val endAddresses = geocoder.getFromLocationName(end, 10)

                    if (beginAddresses.isNotEmpty() && endAddresses.isNotEmpty()) {
                        val waypoints = arrayListOf(
                            GeoPoint(beginAddresses[0].latitude, beginAddresses[0].longitude),
                            GeoPoint(endAddresses[0].latitude, endAddresses[0].longitude)
                        )

                        val roadManager = OSRMRoadManager(requireContext())
                        val roads = roadManager.getRoad(waypoints)

                        if (roads != null) {
                            val roadOverlay = RoadManager.buildRoadOverlay(roads)
                            map.mapView.overlays.add(roadOverlay)

                            requireActivity().runOnUiThread {
                                map.mapView.invalidate()
                            }
                        }
                    }
                }catch (e: IOException) {
                    Log.e(TAG, "onViewCreated: ${e.message}", e)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        compass.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        compass.onPause()
    }

    private fun initMap() {
        map = Map(mapView)
        locationMarker = LocationMarker(
            mapView, ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_expand_less_24,
                requireActivity().theme
            )
        )

        map.mapView.overlays.add(locationMarker)
        compass.listeners.add(Compass.CompassListener {
            locationMarker.rotation = it
            mapView.invalidate()
        })
    }

    private fun setInitialLocation() {
        val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
        try {
            val locationRequest = LocationRequest.create()
            locationRequest.interval = 500
            locationRequest.fastestInterval = 100
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                MapLocationCallback(map, locationMarker),
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "setInitialLocation: ${e.message}", e)
        }
    }

}
