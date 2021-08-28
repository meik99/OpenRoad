package com.rynkbit.openroad.ui.routing

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.osmdroid.bonuspack.location.GeocoderNominatim
import java.util.*
import java.util.concurrent.Executors

class AddressSearch {
    companion object {
        const val DELAY = 500L
        const val MAX_RESULTS = 10
        const val USER_AGENT = "OpenRoad"
    }

    val addresses: LiveData<List<Address>>
        get() = addressesData

    private val addressesData = MutableLiveData<List<Address>>()
    private var executor = Executors.newSingleThreadExecutor()
    private var timer = Timer()

    fun onResume() {
        executor = Executors.newSingleThreadExecutor()
    }

    fun onStop() {
        executor.shutdown()
    }

    fun search(query: String) {
        timer.cancel()
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                getRoadsForQuery(query)
            }
        }, DELAY)
    }

    private fun getRoadsForQuery(query: String) {
        executor.submit {
            val geocoder = GeocoderNominatim(
                Locale.getDefault(),
                USER_AGENT
            )
            val addresses = geocoder.getFromLocationName(query, MAX_RESULTS)
            addressesData.postValue(addresses)
        }
    }
}
