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
            fabSetEndpoint.isEnabled = viewModel.startPoint.value != null
        }

        addressSearch.addresses.observe(viewLifecycleOwner) {
            addressListAdapter.addresses = it
        }

        addressListAdapter.selectedAddress.observe(viewLifecycleOwner) {
            editStart.setText(it.formatDisplayName())
            viewModel.startPoint.postValue(GeoPoint(it.latitude, it.longitude))
        }

        mapViewModel.currentLocation?.let {
            editStart.setText(resources.getString(R.string.longitude_latitude, it.longitude, it.latitude))
            viewModel.startPoint.postValue(it)
        }

        btnClearText.setOnClickListener {
            editStart.text?.clear()
        }

        editStart.addTextChangedListener {
            addressSearch.search(it.toString())
        }

        fabSetEndpoint.setOnClickListener {
            Navigation
                .findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_setStartFragment_to_setEndFragment)
        }

        fabCancel.setOnClickListener {
            Navigation
                .findNavController(requireActivity(), R.id.nav_host_fragment)
                .popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        addressSearch.onResume()
    }

    override fun onPause() {
        super.onPause()
        addressSearch.onPause()
    }
}

