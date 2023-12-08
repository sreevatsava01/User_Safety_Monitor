package com.example.myapplication

<<<<<<< HEAD
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
=======
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
>>>>>>> main
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
<<<<<<< HEAD
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var dynamoDBManager: DynamoDBManager

    private lateinit var locationClient: FusedLocationProviderClient

    private var allPolygons: List<List<LatLng>> = emptyList()

    private val LOCATION_REQUEST_CODE = 101
    private var lastTransitionType: Int? = null

    private lateinit var sensorManager: SensorManager
=======
import org.json.JSONObject

class MainActivity : AppCompatActivity(), RespRateListenerInterface  {
    private lateinit var sensorManager: SensorManager

    private lateinit var respiratoryRateListener: RespiratoryRateListener
    private lateinit var textRR: TextView

    private lateinit var textHR: TextView
    private lateinit var videoRecorder: VideoRecorder
    private lateinit var cameraFloatingWindow: PreviewView

    private lateinit var fuzzyLogicController: FuzzyLogicController
    private val handler = Handler()
>>>>>>> main

    override fun onBackPressed() {
        //do nothign
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
<<<<<<< HEAD
=======
        AllowPermissions(this)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Code for tracking Respiratory Rate
        textRR = findViewById(R.id.MRR)
        respiratoryRateListener = RespiratoryRateListener(this)
        registerRRListener()

        // Code for tracking step count
        // To be implemented

        // Code for recording video and reporting HeartRate
        // Here recording video is required as a dependency for processing the Video to get heart rate.
        // Recording video part has been implemented by Aditya Mettu and I am using it as a dependency here.
        textHR = findViewById(R.id.MHR)
        cameraFloatingWindow = findViewById(R.id.videoView)
        videoRecorder = VideoRecorder(this@MainActivity, cameraFloatingWindow, textHR)
        val recordButton: Button = findViewById(R.id.record)
        recordButton.setOnClickListener {
            videoRecorder.configureCamera()
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                videoRecorder.startVideoCapturing()
            }
        }

        fuzzyLogicController = FuzzyLogicController()
        // Schedule the fuzzy logic evaluation to run every minute
        handler.postDelayed(object : Runnable {
            override fun run() {
                val hr = extractDouble(textHR.text.toString())
                val rr = extractDouble(textRR.text.toString())
                val steps = 0.0 // implemented in another component
                println("Heart Rate: $hr")
                println("Resp Rate: $rr")
                println("Step Rate: $steps")

                val dangerLevel = fuzzyLogicController.evaluateDangerLevel(hr, rr, steps)
                var dangerLevelTag = if (dangerLevel <= 50.0) {
                    "low"
                } else {
                    "high"
                }
                // Update UI or take action based on danger level
                Toast.makeText(this@MainActivity, "Danger Level: $dangerLevelTag", Toast.LENGTH_SHORT).show()

                if (dangerLevelTag == "high") {
                    val jsonObject = JSONObject().apply {
                        put("danger", dangerLevelTag)
                        put("isinside", "true") // Assuming 'false' as a placeholder
                        put("spm", steps.toString())
                        put("lat", "0.0") // Replace with actual latitude for current location
                        put("lon", "0.0") // Replace with actual longitude for current location
                        put("heart_rate", hr.toString())
                        put("respiratory_rate", rr.toString())
                    }

                    println(jsonObject)
                    // Sending the Json object to SQS has been implemented in another component by Sravan. Just adding a placeholder here.
//                    sendToSQS(jsonObject)
                }

                handler.postDelayed(this, 60000) // Reschedule every minute
            }
        }, 60000)
    }

    private fun registerRRListener() {
        val respiratoryRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (respiratoryRateSensor == null) {
            Toast.makeText(this, "Accelerometer Sensor not available!", Toast.LENGTH_SHORT).show()
        } else {
            // Sensor is available, proceed with registering the listener
            sensorManager.registerListener(respiratoryRateListener, respiratoryRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onResume() {
        super.onResume()
        registerRRListener()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(respiratoryRateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Prevent memory leak
    }

    override fun onRespiratoryRateChanged(respiratoryRate: Int) {
        runOnUiThread {
            textRR.text = "Respiratory Rate is : $respiratoryRate"
        }
    }

    private fun AllowPermissions(mainActivity: MainActivity) {
        val PERMISSION_ALL = 1
        val PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        if (!hasPermissions(this@MainActivity, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
    }

    private fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun extractDouble(text: String): Double {
        val numberRegex = Regex("[0-9]+(\\.[0-9]+)?")
        val matchResult = numberRegex.find(text)
        return matchResult?.value?.toDoubleOrNull() ?: 0.0
>>>>>>> main
    }
}