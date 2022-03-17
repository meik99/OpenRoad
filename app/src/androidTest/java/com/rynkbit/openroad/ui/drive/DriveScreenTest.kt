package com.rynkbit.openroad.ui.drive

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.rynkbit.openroad.ui.theme.OpenRoadTheme
import org.junit.Rule
import org.junit.Test

class DriveScreenTest {
    @get: Rule
    val composeTestRule = createComposeRule()

    @Test
    fun DriveButtonIsAtLowerRightCorner() {
        composeTestRule.setContent {
            OpenRoadTheme {
                DriveScreen()
            }
        }

        val buttons = listOf("Navigation", "Trip", "Saved route")

        for (button in buttons) {
            composeTestRule.onNodeWithText(button).assertExists()
            composeTestRule.onNodeWithText(button).assertIsDisplayed()
        }
    }

}