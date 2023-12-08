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
import kotlin.math.max

class MainActivity : AppCompatActivity(), StepCountListener, RespRateListenerInterface  {
    var heartRate: Int = 0
    var respiratoryRate: Int = 0
    var hasmeasureHR: Boolean = false
    var hasmeasureRR: Boolean = false

    private lateinit var dynamoDBManager: DynamoDBManager

    private lateinit var locationClient: FusedLocationProviderClient

    private var allPolygons: List<List<LatLng>> = emptyList()

    private val LOCATION_REQUEST_CODE = 101
    private var lastTransitionType: Int? = null

    private lateinit var sensorManager: SensorManager

    private lateinit var respiratoryRateListener: RespiratoryRateListener
    private lateinit var textRR: TextView

    private lateinit var stepCounterListener: StepCounterListener
    private lateinit var textStepCount: TextView

    private lateinit var textHR: TextView
    private lateinit var videoRecorder: VideoRecorder
    private lateinit var cameraFloatingWindow: PreviewView

    private lateinit var fuzzyLogicController: FuzzyLogicController
    private val handler = Handler()

    private var maxRespRate: Int = 0

    override fun onBackPressed() {
        //do nothign
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AllowPermissions(this)

        dynamoDBManager = DynamoDBManager(this)
        fetchAllPolygons()

        val actionBar: ActionBar? = actionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val sympButton = findViewById<Button>(R.id.symptoms)
        sympButton.setOnClickListener() {
            if (!hasmeasureHR || !hasmeasureRR) {
                Toast.makeText(
                    this@MainActivity,
                    "measurement were not uploaded",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val changeActivity = Intent(this@MainActivity, Symptoms::class.java)
            changeActivity.putExtra("HRmeasurements", heartRate)
            changeActivity.putExtra("RRmeasurements", respiratoryRate)
            changeActivity.putExtra("HRbool", hasmeasureHR)
            changeActivity.putExtra("RRbool", hasmeasureRR)
            startActivity(changeActivity)
        }

        val directionScreenButton = findViewById<Button>(R.id.directionScreen)
        directionScreenButton.setOnClickListener {
            val changeAcivityToDriving = Intent(this@MainActivity, DirectionsScreen::class.java)
            startActivity(changeAcivityToDriving)
        }

        locationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            startLocationUpdates()
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Code for tracking Respiratory Rate
        textRR = findViewById(R.id.MRR)
        respiratoryRateListener = RespiratoryRateListener(this)
        registerRRListener()

        // Code for tracking step count
        textStepCount = findViewById(R.id.textStepCount)
        stepCounterListener = StepCounterListener(this)
        stepCounterListener.resetStepCount()
        textStepCount = findViewById(R.id.textStepCount)
        registerStepCounter()

        // Code for recording video and reporting HeartRate
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
                val steps = extractDouble(textStepCount.text.toString())
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

                    sendToSQS(jsonObject)
                }

                handler.postDelayed(this, 120000) // Reschedule every minute
            }
        }, 120000)
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
        registerRRListener()
        registerStepCounter()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(respiratoryRateListener)
        sensorManager.unregisterListener(stepCounterListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Prevent memory leak
        locationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRespiratoryRateChanged(respiratoryRate: Int) {
        runOnUiThread {
            maxRespRate = max(maxRespRate, respiratoryRate)
            textRR.text = "Respiratory Rate is : $maxRespRate"
        }
    }

    override fun onStepCountChanged(stepCount: Int) {
        runOnUiThread {
            textStepCount.text = "Steps: $stepCount"
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
    }

    private fun fetchAllPolygons() {
        dynamoDBManager.fetchAllPolygons(
            onResult = { polygons ->
                allPolygons = polygons
            },
            onError = { exception ->
                // Display an error message
                Toast.makeText(this, "Error fetching polygons: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000 // Update interval in milliseconds
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                checkGeofenceCrossing(location)
            }
        }
    }


    private fun checkGeofenceCrossing(currentLocation: Location) {
        var isInsideAnyPolygon = false

        for (polygon in allPolygons) {
            if (isPointInsidePolygon(currentLocation, polygon)) {
                isInsideAnyPolygon = true
                break
            }
        }

        Log.d("MainActivity", "Inside polygon $currentLocation $isInsideAnyPolygon")


        val currentTransition = if (isInsideAnyPolygon) {
            Geofence.GEOFENCE_TRANSITION_ENTER
        } else {
            Geofence.GEOFENCE_TRANSITION_EXIT
        }

        if (lastTransitionType == null || lastTransitionType != currentTransition) {
            handleGeofenceTransition(currentTransition)
            lastTransitionType = currentTransition
            val isInside = if (currentTransition == Geofence.GEOFENCE_TRANSITION_ENTER) "true" else "false"
            if(isInside == "false") {
                val jsonObject = JSONObject().apply {
                    put("danger", "high")
                    put("isinside", isInside) // Assuming 'false' as a placeholder
                    put(
                        "lat",
                        currentLocation.latitude.toString()
                    ) // Replace with actual latitude for current location
                    put(
                        "lon",
                        currentLocation.longitude.toString()
                    ) // Replace with actual longitude for current location
                }
                sendToSQS(jsonObject)
            }
        }
    }

    private fun isPointInsidePolygon(point: Location, polygon: List<LatLng>): Boolean {
        var result = false
        var j = polygon.size - 1
        for (i in polygon.indices) {
            if (polygon[i].longitude > point.longitude != (polygon[j].longitude > point.longitude) &&
                point.latitude < (polygon[j].latitude - polygon[i].latitude) *
                (point.longitude - polygon[i].longitude) /
                (polygon[j].longitude - polygon[i].longitude) + polygon[i].latitude) {
                result = !result
            }
            j = i
        }
        return result
    }

    private fun handleGeofenceTransition(transitionType: Int) {
        val message = when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "Entering Geofence"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "Exiting Geofence"
            Geofence.GEOFENCE_TRANSITION_DWELL -> "Dwelling in Geofence"
            else -> "Unknown Geofence Transition"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startLocationUpdates()
            } else {
                // Handle permission denial
            }
        }
    }
}