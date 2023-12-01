package com.example.myapplication

import android.app.ActionBar
import android.Manifest
import android.annotation.SuppressLint
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

class MainActivity : AppCompatActivity(){
    var heartRate: Int = 0
    var respiratoryRate: Int = 0
    var hasmeasureHR: Boolean = false
    var hasmeasureRR: Boolean = false


    private lateinit var textRR: TextView

    private lateinit var textStepCount: TextView

    private lateinit var textHR: TextView
    private lateinit var cameraFloatingWindow: PreviewView

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        //do nothign
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar: ActionBar? = actionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        val uploadSignsButton = findViewById<Button>(R.id.uploadMeasurements)
        uploadSignsButton.setOnClickListener() {
            hasmeasureHR = true
            hasmeasureRR = true
            Toast.makeText(
                this@MainActivity,
                "Recorded Measurement successfully",
                Toast.LENGTH_SHORT
            ).show()
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



        // Code for tracking Respiratory Rate
        textRR = findViewById(R.id.MRR)


        // Code for tracking step count
        textStepCount = findViewById(R.id.textStepCount)


        // Code for recording video and reporting HeartRate
        textHR = findViewById(R.id.MHR)
        cameraFloatingWindow = findViewById(R.id.videoView)
        val recordButton: Button = findViewById(R.id.record)
        recordButton.setOnClickListener {
            //code to record video
        }
    }



















}