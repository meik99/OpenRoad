package com.rynkbit.openroad.ui.map

import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.rynkbit.openroad.R
import com.rynkbit.openroad.location.HighAccuracyLocationRequestBuilder
import com.rynkbit.openroad.ui.map.location.Compass
import com.rynkbit.openroad.ui.map.location.LocationMarker
import kotlinx.android.synthetic.main.map_fragment.*
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Overlay


class MapFragment : Fragment() {
    companion object {
        val TAG = MapFragment::class.java.simpleName
    }

    private val viewModel: MapViewModel by activityViewModels()

    private lateinit var map: Map

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map = Map(mapView)

        Permissions(this).apply {
            listener = Permissions.PermissionsListener { map.setInitialLocation() }
            askRequiredPermissions(getRequiredPermissions())
        }

        map.locationCallback.currentLocation.observe(viewLifecycleOwner) {
            viewModel.currentLocation = it
        }

        fabCenter.setOnClickListener {
            map.locationCallback.toggleCenter()
            if (map.locationCallback.isCentering) {
                fabCenter.drawable.setTint(
                    resources.getColor(
                        R.color.colorPrimaryDark,
                        requireContext().theme
                    )
                )
            } else {
                fabCenter.drawable.setTint(
                    resources.getColor(
                        R.color.colorIconDefault,
                        requireContext().theme
                    )
                )
            }
        }

        fabRoute.setOnClickListener {
            Navigation
                .findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_mapView_to_setStartFragment)
        }

        viewModel.routes.observe(viewLifecycleOwner) { roads ->
            val roadOverlays = map.showRoads(roads)

            map.mapView.overlays.removeAll(viewModel.roadOverlays)
            map.mapView.overlays.addAll(roadOverlays)

            viewModel.roadOverlays = roadOverlays

            requireActivity().runOnUiThread {
                map.mapView.invalidate()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        map.compass.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        map.compass.onPause()
    }


}
