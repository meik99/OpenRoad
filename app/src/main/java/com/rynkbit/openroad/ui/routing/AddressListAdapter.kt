package com.rynkbit.openroad.ui.routing

import android.annotation.SuppressLint
import android.location.Address
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.rynkbit.openroad.R
import kotlinx.android.synthetic.main.item_address.view.*
import java.util.*

class AddressListAdapter : RecyclerView.Adapter<AddressListAdapter.AddressListViewHolder>() {
    companion object {
        const val EXTRA_DISPLAY_NAME = "display_name"
    }
    var addresses = listOf<Address>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val selectedAddress: LiveData<Address>
        get() = selectedAddressData

    private val selectedAddressData = MutableLiveData<Address>()

    class AddressListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun applyAddress(address: Address) {
            itemView.btnAddress.text = address.formatDisplayName()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressListViewHolder {
        return AddressListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AddressListViewHolder, position: Int) {
        holder.applyAddress(addresses[position])
        holder.itemView.btnAddress.setOnClickListener {
            selectedAddressData.postValue(addresses[position])
        }
    }

    override fun getItemCount(): Int = addresses.size
}

fun Address.formatDisplayName(): String {
    val addressLine = extras.getString(AddressListAdapter.EXTRA_DISPLAY_NAME, "")
    if (addressLine.isNullOrBlank()) {
        return format()
    }
    return addressLine
}

fun Address.format(): String {
    val addressLine = mutableListOf<String>()

    for (i in 0..maxAddressLineIndex) {
        addressLine.add(getAddressLine(i).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        })
    }

    return addressLine.joinToString(separator = ", ")
}