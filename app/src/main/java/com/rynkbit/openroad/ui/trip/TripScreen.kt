package com.rynkbit.openroad.ui.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rynkbit.openroad.ui.mapView.mapViewFactory
import com.rynkbit.openroad.ui.mapView.setMapViewDefaults
import com.rynkbit.openroad.ui.theme.MarginDefaultX
import com.rynkbit.openroad.ui.theme.MarginDefaultY
import com.rynkbit.openroad.ui.theme.OpenRoadTheme
import com.rynkbit.openroad.ui.theme.basicTextFieldColors

@Preview
@Composable
fun TripScreen(navController: NavHostController = rememberNavController()) {
    OpenRoadTheme {
        Box(
            contentAlignment = Alignment.TopCenter
        ) {
            Map()
            StartLocation()
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
private fun StartLocation() {
    val text = remember {
        mutableStateOf("")
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = MarginDefaultX, end = MarginDefaultX, top = MarginDefaultY),
        label = { Text(text = "Start location") },
        value = text.value,
        onValueChange = { text.value = it },
        colors = basicTextFieldColors(),
    )
}
