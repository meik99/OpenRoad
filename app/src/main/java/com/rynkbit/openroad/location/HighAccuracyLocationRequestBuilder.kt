package com.rynkbit.openroad.location

import com.google.android.gms.location.LocationRequest

class HighAccuracyLocationRequestBuilder {
    fun build(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 500
            fastestInterval = 100
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}