package com.rynkbit.openroad.ui.mapView

import android.content.Context
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


fun mapViewFactory(localLifecycleOwner: State<LifecycleOwner>) = { context: Context ->
    Configuration
        .getInstance()
        .load(context, PreferenceManager.getDefaultSharedPreferences(context))

    val mapView = MapView(context)
    mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

    localLifecycleOwner.value.lifecycle.addObserver(mapLifecycleEventObserver(mapView))
    mapView
}

private fun mapLifecycleEventObserver(mapView: MapView) = LifecycleEventObserver { _, event ->
    when (event) {
        Lifecycle.Event.ON_RESUME -> mapView.onResume()
        Lifecycle.Event.ON_PAUSE -> mapView.onPause()
        else -> {}
    }
}

fun setMapViewDefaults(mapView: MapView) {
    mapView.controller.setZoom(12.0)
    mapView.controller.setCenter(GeoPoint(40.423281, -98.02920))
}
