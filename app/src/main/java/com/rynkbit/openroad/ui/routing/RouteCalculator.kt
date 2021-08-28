package com.rynkbit.openroad.ui.routing

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Overlay
import java.util.concurrent.Executors

class RouteCalculator(private val context: Context) {
//    val road: LiveData<Road>
//        get() = roadData
//    val roadOverlay: LiveData<Overlay>
//        get() = roadOverlayData
    val alternativeRoads: LiveData<Array<Road>>
        get() = alternativeRoadsData

//    private val roadData = MutableLiveData<Road>()
//    private val roadOverlayData = MutableLiveData<Overlay>()
    private val alternativeRoadsData = MutableLiveData<Array<Road>>()

    fun calculateRoutes(start: GeoPoint, end: GeoPoint) {
        Executors.newSingleThreadExecutor().submit {
            val waypoints = arrayListOf(start, end)
            val roadManager = OSRMRoadManager(context)
            val roads = roadManager.getRoads(waypoints)

            alternativeRoadsData.postValue(roads)
        }
    }
}


//                      val waypoints = arrayListOf(
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