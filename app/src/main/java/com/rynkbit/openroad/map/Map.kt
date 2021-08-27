package com.rynkbit.openroad.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.rynkbit.openroad.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.concurrent.Executors


class Map : Fragment() {
    private lateinit var viewModel: MapViewModel
    private lateinit var map: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
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

        askRequiredPermissions(getRequiredPermissions())
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }


    private fun setInitialLocation() {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnSuccessListener {
                val point = GeoPoint(it.latitude, it.longitude)
                val marker = Marker(map)

                marker.position = point
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP)
                marker.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_expand_less_24, requireActivity().theme)

                map.overlays.add(marker)
                map.controller.setCenter(point)
                map.controller.setZoom(19.0)
                map.postInvalidate()
                map.invalidate()
            }
        } catch (e: SecurityException) {
            Log.e(Map::class.java.simpleName, e.message ?: "")
        }
    }

    private fun askRequiredPermissions(requiredPermissions: List<String>) {
        if (requiredPermissions.isNotEmpty()) {
            val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
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

        if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)  {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        return permissions
    }
}
