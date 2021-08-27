package com.rynkbit.openroad.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class Permissions(val fragment: Fragment) {
    fun interface PermissionsListener {
        fun onGranted()
    }

    var listener: PermissionsListener? = null

    fun askRequiredPermissions(requiredPermissions: List<String>) {
        if (requiredPermissions.isNotEmpty()) {
            val requestPermissionLauncher =
                fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    if (isGranted) {
                        listener?.onGranted()
                    }
                }

            for (permission in requiredPermissions) {
                requestPermissionLauncher.launch(permission)
            }
        } else {
            listener?.onGranted()
        }
    }

    fun getRequiredPermissions(): List<String> {
        val context = fragment.requireContext()
        val permissions = mutableListOf<String>()

        if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        return permissions
    }
}