package com.rynkbit.openroad

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import java.util.*


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SetupScenario {
    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity>
            = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun startAddressSearch() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.rynkbit.openroad", appContext.packageName)

        onView(withId(R.id.fabRoute)).perform(click())
        onView(withId(R.id.btnClearText)).perform(click())
        onView(withId(R.id.editStart)).perform(typeText(BuildConfig.TEST_START_ADDRESS))

        Thread.sleep(3600000L)
    }

    @Test
    fun calculateRoute() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.rynkbit.openroad", appContext.packageName)

        onView(withId(R.id.fabRoute)).perform(click())
        onView(withId(R.id.btnClearText)).perform(click())
        onView(withId(R.id.editStart)).perform(typeText(BuildConfig.TEST_START_ADDRESS))
        Thread.sleep(2000)
        onView(withId(R.id.fabSetEndpoint)).perform(click())
        Thread.sleep(1000)

        onView(withId(R.id.editEnd)).perform(typeText(BuildConfig.TEST_END_ADDRESS))
        Thread.sleep(1000)
        onView(withId(R.id.fabStartNavigation)).perform(click())

        Thread.sleep(3600000L)
    }
}
