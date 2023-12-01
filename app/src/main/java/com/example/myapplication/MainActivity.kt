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

class MainActivity : AppCompatActivity() {
    private lateinit var dynamoDBManager: DynamoDBManager

    private lateinit var locationClient: FusedLocationProviderClient

    private var allPolygons: List<List<LatLng>> = emptyList()

    private val LOCATION_REQUEST_CODE = 101
    private var lastTransitionType: Int? = null

    private lateinit var sensorManager: SensorManager

    override fun onBackPressed() {
        //do nothign
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}