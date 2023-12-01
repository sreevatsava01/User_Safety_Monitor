package com.example.myapplication

import android.app.ActionBar
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class MainActivity : AppCompatActivity(), StepCountListener  {

    private lateinit var sensorManager: SensorManager
    private lateinit var stepCounterListener: StepCounterListener
    private lateinit var textStepCount: TextView

    private val handler = Handler()

    override fun onBackPressed() {
        //do nothign
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Code for tracking step count
        textStepCount = findViewById(R.id.textStepCount)
        stepCounterListener = StepCounterListener(this)
        stepCounterListener.resetStepCount()
        textStepCount = findViewById(R.id.textStepCount)
        registerStepCounter()

        handler.postDelayed(object : Runnable {
            override fun run() {
                val steps = extractDouble(textStepCount.text.toString())
                println("Step Rate: $steps")

                    val jsonObject = JSONObject().apply {
                        put("danger", "high")
                        put("isinside", "true") // Assuming 'false' as a placeholder
                        put("spm", steps.toString())
                        put("lat", "0.0") // Replace with actual latitude for current location
                        put("lon", "0.0") // Replace with actual longitude for current location
                    }

                    sendToSQS(jsonObject)

                handler.postDelayed(this, 60000) // Reschedule every minute
            }
        }, 60000)
    }

    private fun registerStepCounter() {
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepCounterSensor == null) {
            Toast.makeText(this, "Step Counter Sensor not available!", Toast.LENGTH_SHORT).show()
        } else {
            // Sensor is available, proceed with registering the listener
            sensorManager.registerListener(stepCounterListener, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onResume() {
        super.onResume()
        registerStepCounter()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(stepCounterListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Prevent memory leak
    }

    override fun onStepCountChanged(stepCount: Int) {
        runOnUiThread {
            textStepCount.text = "Steps: $stepCount"
        }
    }

    private fun extractDouble(text: String): Double {
        val numberRegex = Regex("[0-9]+(\\.[0-9]+)?")
        val matchResult = numberRegex.find(text)
        return matchResult?.value?.toDoubleOrNull() ?: 0.0
    }
}