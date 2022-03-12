package com.rynkbit.openroad.ui.map

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.preference.PreferenceManager
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


@Composable
fun MapScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Map()
        Search()
    }
}

@Composable
private fun Map() {
    val localLifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = mapViewFactory(localLifecycleOwner),
        update = { mapView ->
            setMapViewDefaults(mapView)
        }
    )
}

@Preview
@Composable
private fun Search() {
    val searchInput = remember {
        mutableStateOf("")
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dp(8f)),
        value = searchInput.value,
        onValueChange = { searchInput.value = it },
        placeholder = {  },
        label = { Text(
            text = "Where do you want to go?",
            color = MaterialTheme.colorScheme.primary) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = MaterialTheme.colorScheme.background,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
        )
    )
}

@Composable
private fun mapViewFactory(localLifecycleOwner: State<LifecycleOwner>) = { context: Context ->
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

private fun setMapViewDefaults(mapView: MapView) {
    mapView.controller.setZoom(12.0)
    mapView.controller.setCenter(GeoPoint(40.423281, -98.02920))
}
