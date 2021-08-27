package com.rynkbit.openroad.ui.map

import android.graphics.drawable.Drawable
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class LocationMarker(mapView: MapView?, locationDrawable: Drawable?) : Marker(mapView) {
    init {
        setAnchor(ANCHOR_CENTER, ANCHOR_CENTER)
        icon = locationDrawable
    }
}