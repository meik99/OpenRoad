package com.rynkbit.openroad.ui.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rynkbit.openroad.R
import com.rynkbit.openroad.ui.mapView.mapViewFactory
import com.rynkbit.openroad.ui.mapView.setMapViewDefaults
import com.rynkbit.openroad.ui.theme.MarginDefaultX
import com.rynkbit.openroad.ui.theme.MarginDefaultY
import com.rynkbit.openroad.ui.theme.OpenRoadTheme


@Composable
fun MapScreen(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val showPermissionDialog = remember {
        mutableStateOf(false)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.containsValue(false)) {
            showPermissionDialog.value = true
        }
    }

    OpenRoadTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Map()
            DriveFab(
                modifier = Modifier.offset(-MarginDefaultX, -MarginDefaultY),
                navController = navController
            )

            if (showPermissionDialog.value) {
                PermissionDialog()
            }
        }
    }

    SideEffect {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PERMISSION_GRANTED
        ) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
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
private fun DriveFab(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = {
            navController.navigate("drive")
        }
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
