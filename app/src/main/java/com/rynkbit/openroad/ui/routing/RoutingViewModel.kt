package com.rynkbit.openroad.ui.routing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint

class RoutingViewModel : ViewModel() {
    var startPoint = MutableLiveData<GeoPoint>()
}