package com.rynkbit.openroad.ui.drive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rynkbit.openroad.R

@Preview
@Composable
fun DriveScreen(navController: NavHostController = rememberNavController()) {
    Column(modifier = Modifier.fillMaxSize()) {
        DriveButton(
            text = "Navigation",
            painter = painterResource(id = R.drawable.ic_baseline_directions_24),
        )
        DriveButton(
            text = "Trip",
            painter = painterResource(id = R.drawable.ic_baseline_mode_of_travel_24),
            onClick = { navController.navigate("trip") }
        )
        DriveButton(
            text = "Saved route",
            painter = painterResource(id = R.drawable.ic_baseline_save_24),
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
private fun DriveButton(
    text: String = "Navigation",
    painter: Painter = painterResource(id = R.drawable.ic_baseline_directions_24),
    onClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        text = { Text(text = text) },
        icon = {
            Icon(
                modifier = Modifier
                    .clipToBounds()
                    .padding(Dp(8f)),
                painter = painter,
                contentDescription = null
            )
        }
    )
    Divider()
}