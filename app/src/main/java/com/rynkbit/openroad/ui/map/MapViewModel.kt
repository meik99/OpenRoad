package com.rynkbit.openroad.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Overlay

class MapViewModel : ViewModel() {
    var currentLocation: GeoPoint? = null
    val routes = MutableLiveData<Array<Road>>()
    var roadOverlays: List<Overlay> = listOf()
}
