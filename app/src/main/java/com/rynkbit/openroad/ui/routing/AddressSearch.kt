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

    fun onPause() {
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
        executor.execute {
            val geocoder = GeocoderNominatim(
                Locale.getDefault(),
                USER_AGENT
            )
            val addresses = geocoder.getFromLocationName(query, MAX_RESULTS)
            addressesData.postValue(addresses)
        }
    }
}

//
//        btnCalcRoute.setOnClickListener {
//            val begin = editBegin.text.toString()
//            val end = editEnd.text.toString()
//
//            Executors.newSingleThreadExecutor().execute {
//                try {
//                    val geocoder =  GeocoderNominatim(
//                        Locale.getDefault(),
//                        "OpenRoad"
//                    )
//
//                    val beginAddresses = geocoder.getFromLocationName(begin, 10)
//                    val endAddresses = geocoder.getFromLocationName(end, 10)
//
//                    if (beginAddresses.isNotEmpty() && endAddresses.isNotEmpty()) {
//                        val waypoints = arrayListOf(
//                            GeoPoint(beginAddresses[0].latitude, beginAddresses[0].longitude),
//                            GeoPoint(endAddresses[0].latitude, endAddresses[0].longitude)
//                        )
//
//                        val roadManager = OSRMRoadManager(requireContext())
//                        val roads = roadManager.getRoad(waypoints)
//
//                        if (roads != null) {
//                            val roadOverlay = RoadManager.buildRoadOverlay(roads)
//                            map.mapView.overlays.add(roadOverlay)
//
//                            requireActivity().runOnUiThread {
//                                map.mapView.invalidate()
//                            }
//                        }
//                    }
//                }catch (e: IOException) {
//                    Log.e(TAG, "onViewCreated: ${e.message}", e)
//                }
//            }
//
//        }