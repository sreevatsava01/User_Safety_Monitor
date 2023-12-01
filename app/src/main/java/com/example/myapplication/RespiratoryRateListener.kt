package com.example.myapplication
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.TextView
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class RespiratoryRateListener(private val respRateListenerInterface: RespRateListenerInterface) : SensorEventListener {
    private val accValX = FloatArray(451)
    private val accValY = FloatArray(451)
    private val accValZ = FloatArray(451)
    private var ik = 0
        private set

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val sensor: Sensor = sensorEvent.sensor
        var accdata = 0
        if (sensor.type == Sensor.TYPE_ACCELEROMETER) {
            ik++
            accValX[ik] = sensorEvent.values[0]
            accValY[ik] = sensorEvent.values[1]
            accValZ[ik] = sensorEvent.values[2]
            if (ik >= 450) {
                ik = 0
                accdata = callRespiratoryCalculator()
                respRateListenerInterface.onRespiratoryRateChanged(accdata)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    private fun callRespiratoryCalculator(): Int {
        var prevValue = 0f
        var currValue = 0f
        prevValue = 10f
        var k = 0
        for (i in 11..450) {
            currValue = sqrt(
                accValZ[i].toDouble().pow(2.0) + accValX[i].toDouble().pow(2.0) + accValY[i].toDouble().pow(2.0)
            ).toFloat()
            if (abs(prevValue - currValue) > 0.15) {
                k++
            }
            prevValue = currValue
        }
        val ret = k.toDouble() / 45.00
        return (ret * 30).toInt()
    }
}

interface RespRateListenerInterface {
    fun onRespiratoryRateChanged(respiratoryRate: Int)
}