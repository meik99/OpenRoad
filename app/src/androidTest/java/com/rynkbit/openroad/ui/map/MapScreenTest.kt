package com.rynkbit.openroad.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.rynkbit.openroad.ui.theme.OpenRoadTheme
import org.junit.Rule
import org.junit.Test

class MapScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Test
    fun DriveButtonIsAtLowerRightCorner() {
        composeTestRule.setContent {
            OpenRoadTheme {
                MapScreen()
            }
        }

        composeTestRule.onNodeWithText("Drive").assertIsDisplayed()
    }
}