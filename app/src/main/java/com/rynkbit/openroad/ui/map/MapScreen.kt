package com.rynkbit.openroad.ui.map

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.preference.PreferenceManager
import androidx.compose.material.Icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.PermissionResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.rynkbit.openroad.R
import com.rynkbit.openroad.ui.theme.MarginDefaultX
import com.rynkbit.openroad.ui.theme.MarginDefaultY
import com.rynkbit.openroad.ui.theme.OpenRoadTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


@Composable
fun MapScreen() {
    val context = LocalContext.current
    val showPermissionDialog = remember {
        mutableStateOf(false)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.containsValue(false)) {
            showPermissionDialog.value = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Map()
        DriveFab(Modifier.offset(-MarginDefaultX, -MarginDefaultY))

        if (showPermissionDialog.value) {
            PermissionDialog()
        }
    }

    SideEffect {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            launcher.launch(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
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
private fun DriveFab(modifier: Modifier = Modifier) {
    FloatingActionButton(
        modifier = modifier,
        onClick = { /*TODO*/ }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .clipToBounds()
                    .padding(Dp(8f)),
                painter = painterResource(id = R.drawable.ic_baseline_two_wheeler_24),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .clipToBounds()
                    .padding(
                        top = Dp(8f), end = Dp(8f), bottom = Dp(8f)
                    ),
                text = stringResource(R.string.drive),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
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
