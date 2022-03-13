package com.rynkbit.openroad.ui.map

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rynkbit.openroad.R
import kotlin.system.exitProcess

@Preview
@Composable
fun PermissionDialog() {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        title = { Title() },
        text = { Text() },
        confirmButton = { ConfirmButton() },
    )
}

@Composable
private fun Title() {
    Text(
        text = stringResource(R.string.permissions)
    )
}

@Composable
private fun Text() {
    Text(
        text = stringResource(R.string.location_permissions_explanation)
    )
}

@Composable
private fun ConfirmButton() {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = {
            exitProcess(0)
        }
    ) {
        Text(text = stringResource(R.string.ok))
    }
}