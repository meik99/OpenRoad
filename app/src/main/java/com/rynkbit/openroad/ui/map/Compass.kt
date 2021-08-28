package com.rynkbit.openroad.ui.map

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


class Compass(context: Context): SensorEventListener {
    fun interface CompassListener {
        fun onRotation(orientation: Float)
    }

    val listeners = mutableListOf<CompassListener>()

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotation: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private var r = FloatArray(9)
    private var orientation = FloatArray(3)

    fun onResume() {
        sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_FASTEST)
    }

    fun onPause() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.apply {
            if (sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(r, values)
                val orientation = SensorManager.getOrientation(r, orientation)
                val rotation = -SensorManager.getOrientation(r, orientation)[0] * 360 / (2 * 3.14159f)

                for (listener in listeners) {
                    listener.onRotation(rotation)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }
}