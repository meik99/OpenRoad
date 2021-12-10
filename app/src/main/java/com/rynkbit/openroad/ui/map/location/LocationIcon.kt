package com.rynkbit.openroad.ui.map.location

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.rynkbit.openroad.R

class LocationIcon(context: Context) {
    val icon: Drawable

    init {
        icon = ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.ic_baseline_navigation_24,
            context.theme
        )!!
        icon.setTint(context.resources.getColor(R.color.colorNavigationIcon, context.theme))
    }
}