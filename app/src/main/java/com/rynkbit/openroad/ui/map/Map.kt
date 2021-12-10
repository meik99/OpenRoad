package com.rynkbit.openroad.ui.map

import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.rynkbit.openroad.R
import com.rynkbit.openroad.location.HighAccuracyLocationRequestBuilder
import com.rynkbit.openroad.ui.map.location.Compass
import com.rynkbit.openroad.ui.map.location.LocationIcon
import com.rynkbit.openroad.ui.map.location.LocationMarker
import kotlinx.android.synthetic.main.map_fragment.*
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

class Map(val mapView: MapView) {
    val context: Context = mapView.context
    val compass = Compass(context)

    private val locationIcon: LocationIcon = LocationIcon(context)
    private val locationMarker: LocationMarker = LocationMarker(mapView, locationIcon.icon)

    val locationCallback = MapLocationCallback(this, locationMarker)

    init {
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(19.0)
        mapView.controller.setCenter(GeoPoint(48.8583, 2.2944))

        mapView.overlays.add(locationMarker)

        compass.listeners.add(Compass.CompassListener {
            locationMarker.rotation = it
            mapView.invalidate()
        })
    }

    fun setInitialLocation() {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)
        try {
            fusedLocationClient.requestLocationUpdates(
                HighAccuracyLocationRequestBuilder().build(),
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e(MapFragment.TAG, "setInitialLocation: ${e.message}", e)
        }
    }

    fun showRoads(roads: Array<Road>): MutableList<Overlay> {
        val resources = context.resources
        val roadOverlays = mutableListOf<Overlay>()

        for (road in roads) {
            if (roadOverlays.isEmpty()) {
                val roadOverlay = RoadManager.buildRoadOverlay(
                    road,
                    resources.getColor(R.color.colorNavigationRoute, context.theme),
                    20f
                )
                roadOverlays.add(roadOverlay)
            } else {
                roadOverlays.add(
                    RoadManager.buildRoadOverlay(
                        road,
                        resources.getColor(android.R.color.darker_gray, context.theme),
                        10f
                    )
                )
            }
        }

        roadOverlays.reverse()
        return roadOverlays
    }
}