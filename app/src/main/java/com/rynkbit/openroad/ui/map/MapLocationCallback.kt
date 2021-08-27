package com.rynkbit.openroad.ui.map

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import org.osmdroid.util.GeoPoint

class MapLocationCallback(val map: Map, val marker: LocationMarker): LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)

        val point = GeoPoint(
            locationResult.lastLocation.latitude,
            locationResult.lastLocation.longitude
        )

        marker.position = point

        map.mapView.controller.setCenter(point)
        map.mapView.invalidate()
    }
}