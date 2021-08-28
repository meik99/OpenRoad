package com.rynkbit.openroad.ui.routing

import android.opengl.Visibility
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rynkbit.openroad.R
import com.rynkbit.openroad.ui.map.MapViewModel
import kotlinx.android.synthetic.main.set_end_fragment.*
import kotlinx.android.synthetic.main.set_start_fragment.*
import kotlinx.android.synthetic.main.set_start_fragment.btnClearText
import kotlinx.android.synthetic.main.set_start_fragment.listAddresses
import org.osmdroid.util.GeoPoint

class SetEndFragment : Fragment() {
    companion object {
        val TAG = SetEndFragment::class.java.simpleName
    }

    private val viewModel: RoutingViewModel by activityViewModels()
    private val mapViewModel: MapViewModel by activityViewModels()
    private val addressSearch = AddressSearch()
    private val addressListAdapter = AddressListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.set_end_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val routeCalculator = RouteCalculator(requireContext())

        listAddresses.adapter = addressListAdapter
        listAddresses.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        listAddresses.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        fabStartNavigation.isEnabled = false

        viewModel.endPoint.observe(viewLifecycleOwner) {
            fabStartNavigation.isEnabled = addressListAdapter.addresses.isNotEmpty()
        }

        addressSearch.addresses.observe(viewLifecycleOwner) {
            addressListAdapter.addresses = it
            fabStartNavigation.isEnabled = addressListAdapter.addresses.isNotEmpty()
        }

        addressListAdapter.selectedAddress.observe(viewLifecycleOwner) {
            editEnd.setText(it.formatDisplayName())
        }

        btnClearText.setOnClickListener {
            editEnd.text?.clear()
        }

        editEnd.addTextChangedListener {
            addressSearch.search(it.toString())
        }

        fabStartNavigation.setOnClickListener {
            val startPoint = viewModel.startPoint.value!!
            val address = addressListAdapter.addresses[0]

            fabStartNavigation.hide()
            progressBar.visibility = View.VISIBLE

            routeCalculator
                .calculateRoutes(startPoint, GeoPoint(address.latitude, address.longitude))
        }

        routeCalculator.alternativeRoads.observe(viewLifecycleOwner) {
            mapViewModel.routes.postValue(it)

            fabStartNavigation.show()
            progressBar.visibility = View.GONE

            Navigation
                .findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_setEndFragment_to_mapView)
        }
    }

    override fun onResume() {
        super.onResume()
        addressSearch.onResume()
    }

    override fun onStop() {
        super.onStop()
        addressSearch.onStop()
    }
}