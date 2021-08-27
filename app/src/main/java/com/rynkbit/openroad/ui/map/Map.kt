package com.rynkbit.openroad.ui.map

import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

class Map(val mapView: MapView) {
    init {
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(19.0)
        mapView.controller.setCenter(GeoPoint(48.8583, 2.2944))
    }
}