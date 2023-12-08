package com.example.myapplication

//import com.google.maps.model.LatLng

//import android.R

import android.Manifest
import android.app.ActionBar
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import org.json.JSONObject
import java.util.Arrays


class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private val TAG = "MapsActivity"
    private var mMap: GoogleMap? = null
    private var binding: ActivityMapsBinding? = null
    var geofencingClient: GeofencingClient? = null
    private val GEOFENCE_RADIUS = 100.0
    private val GEOFENCE_ID = "SOME_GEOFENCE_ID"
    private val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002

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
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        AllowPermissions(this@MapsActivity)

        dynamoDBManager = DynamoDBManager(this)
        fetchAllPolygons()

        val actionBar: ActionBar? = actionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker and move the camera
        val home = LatLng(33.4072, -111.9199)
        mMap?.addMarker(MarkerOptions().position(home).title("Home"))
        val zoomLevel = 16.0f //This goes up to 21
        mMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                home, zoomLevel
            )
        )
        val polygonExample1: Polygon = googleMap.addPolygon(
            PolygonOptions()
                .add(LatLng(33.408210082137195,-111.91948313266039),
                    LatLng(33.408210082137195,-111.91842835396528),
                    LatLng(33.40668809164925,-111.91825702786446),
                    LatLng(33.406563823502914,-111.91950559616089))
                .strokeColor(Color.RED)
                .strokeWidth(5f) // Increase the stroke width for better visibility
                .strokePattern(Arrays.asList(Dash(30f), Gap(20f))) // Creates a dashed pattern for the stroke
                .fillColor(0x550000FF) // Blue color with some transparency
        )

        val polygonExample2: Polygon = googleMap.addPolygon(
            PolygonOptions()
                .add( LatLng(33.407622616000666, -111.9177108630538),
                    LatLng(33.40621396853258, -111.91765051335096),
                    LatLng(33.406253992012566,-111.91558990627527),
                    LatLng(33.407550127050044, -111.91550608724356))
                .strokeColor(Color.RED)
                .strokeWidth(5f) // Increase the stroke width for better visibility
                .strokePattern(Arrays.asList(Dash(30f), Gap(20f))) // Creates a dashed pattern for the stroke
                .fillColor(0x550000FF) // Blue color with some transparency
        )

    }

    override fun onMapLongClick(p0: LatLng) {
        TODO("Not yet implemented")
    }

    private fun AllowPermissions(mainActivity: MapsActivity) {
        val PERMISSION_ALL = 1
        val PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        if (!hasPermissions(this@MapsActivity, *PERMISSIONS)) {
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
                System.out.println(allPolygons)
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
        if(isInsideAnyPolygon){
            Toast.makeText(this, " Inside Geo fence ", Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(this, " Outside Geo fence ", Toast.LENGTH_LONG).show()
        }


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
        println(message)
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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