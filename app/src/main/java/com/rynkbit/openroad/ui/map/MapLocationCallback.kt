package com.rynkbit.openroad.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import org.osmdroid.util.GeoPoint

class MapLocationCallback(val map: Map, val marker: LocationMarker): LocationCallback() {
    val isCentering: Boolean
        get() = continuouslyCenter

    val currentLocation: LiveData<GeoPoint>
        get() = currentLocationData

    private var currentLocationData = MutableLiveData<GeoPoint>()
    private var initialCentered = false
    private var continuouslyCenter = false


    override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)

        val point = GeoPoint(
            locationResult.lastLocation.latitude,
            locationResult.lastLocation.longitude
        )
        currentLocationData.postValue(point)
        marker.position = point
        if (!initialCentered || continuouslyCenter) {
            map.mapView.controller.setCenter(point)
            initialCentered = true
        }

        map.mapView.invalidate()
    }

    fun toggleCenter() {
        continuouslyCenter = !continuouslyCenter
    }
}