package com.rynkbit.openroad.ui.map

import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.rynkbit.openroad.R
import kotlinx.android.synthetic.main.map_fragment.*
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Overlay


class MapFragment : Fragment() {
    companion object {
        val TAG = MapFragment::class.java.simpleName
    }

    private val viewModel: MapViewModel by activityViewModels()

    private lateinit var compass: Compass
    private lateinit var map: Map
    private lateinit var locationMarker: LocationMarker
    private lateinit var locationCallback: MapLocationCallback

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

        compass = Compass(requireContext())

        initMap()
        Permissions(this).apply {
            listener = Permissions.PermissionsListener { setInitialLocation() }
            askRequiredPermissions(getRequiredPermissions())
        }

        locationCallback.currentLocation.observe(viewLifecycleOwner, {
            viewModel.currentLocation = it
        })

        fabCenter.setOnClickListener {
            locationCallback.toggleCenter()
            if (locationCallback.isCentering) {
                fabCenter.drawable.setTint(
                    resources.getColor(
                        R.color.colorPrimaryDark,
                        requireContext().theme
                    )
                )
            } else {
                fabCenter.drawable.setTint(
                    resources.getColor(
                        R.color.colorIconDefault,
                        requireContext().theme
                    )
                )
            }
        }

        fabRoute.setOnClickListener {
            Navigation
                .findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_mapView_to_setStartFragment)
        }

        viewModel.routes.observe(viewLifecycleOwner) {
            showRoads(it)
        }
    }

    private fun showRoads(roads: Array<Road>) {
        val roadOverlays = mutableListOf<Overlay>()
        for (road in roads) {
            if (roadOverlays.isEmpty()) {
                roadOverlays.add(
                    RoadManager.buildRoadOverlay(
                        road,
                        resources.getColor(R.color.colorPrimaryDark, requireContext().theme),
                        10f
                    )
                )
            } else {
                roadOverlays.add(
                    RoadManager.buildRoadOverlay(
                        road,
                        resources.getColor(android.R.color.darker_gray, requireContext().theme),
                        10f
                    )
                )
            }
        }
        roadOverlays.reverse()

        map.mapView.overlays.removeAll(viewModel.roadOverlays)
        viewModel.roadOverlays = roadOverlays
        map.mapView.overlays.addAll(roadOverlays)

        requireActivity().runOnUiThread {
            map.mapView.invalidate()
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
        val icon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_baseline_navigation_24,
            requireContext().theme
        )
        icon?.setTint(resources.getColor(R.color.colorPrimaryDark, requireContext().theme))

        locationMarker = LocationMarker(mapView, icon)
        locationCallback = MapLocationCallback(map, locationMarker)
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
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "setInitialLocation: ${e.message}", e)
        }
    }

}
