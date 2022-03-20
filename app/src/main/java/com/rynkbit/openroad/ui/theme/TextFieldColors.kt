package com.rynkbit.openroad.ui.theme

import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun basicTextFieldColors() =
    TextFieldDefaults.outlinedTextFieldColors(
        backgroundColor = MaterialTheme.colorScheme.background,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        cursorColor = MaterialTheme.colorScheme.primary,
    )
