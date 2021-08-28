package com.rynkbit.openroad.ui.routing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rynkbit.openroad.R
import com.rynkbit.openroad.ui.map.MapViewModel
import kotlinx.android.synthetic.main.set_start_fragment.*
import org.osmdroid.util.GeoPoint

class SetStartFragment : Fragment() {
    private val viewModel: RoutingViewModel by activityViewModels()
    private val mapViewModel: MapViewModel by activityViewModels()
    private val addressSearch = AddressSearch()
    private val addressListAdapter = AddressListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.set_start_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAddresses.adapter = addressListAdapter
        listAddresses.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        listAddresses.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        fabSetEndpoint.isEnabled = false

        viewModel.startPoint.observe(viewLifecycleOwner) {
            fabSetEndpoint.isEnabled = mapViewModel.currentLocation != null || addressListAdapter.addresses.isNotEmpty()
        }

        addressSearch.addresses.observe(viewLifecycleOwner) {
            addressListAdapter.addresses = it
            fabSetEndpoint.isEnabled = mapViewModel.currentLocation != null || addressListAdapter.addresses.isNotEmpty()
        }

        addressListAdapter.selectedAddress.observe(viewLifecycleOwner) {
            editStart.setText(it.formatDisplayName())
        }

        mapViewModel.currentLocation?.let {
            editStart.setText(resources.getString(R.string.current_location))
            viewModel.startPoint.postValue(it)
        }

        btnClearText.setOnClickListener {
            editStart.text?.clear()
        }

        editStart.addTextChangedListener {
            addressSearch.search(it.toString())
        }

        fabSetEndpoint.setOnClickListener {
            if (addressListAdapter.addresses.isNotEmpty()) {
                val address = addressListAdapter.addresses[0]
                viewModel.startPoint.postValue(GeoPoint(address.latitude, address.longitude))
            }
            Navigation
                .findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_setStartFragment_to_setEndFragment)
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

