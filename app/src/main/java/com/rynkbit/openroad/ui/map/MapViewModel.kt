package com.rynkbit.openroad.ui.map

import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint

class MapViewModel : ViewModel() {
    var currentLocation: GeoPoint? = null
}
