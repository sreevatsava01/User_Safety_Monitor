package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.ui.AppBarConfiguration
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.amazonaws.services.dynamodbv2.model.ScanResult
import com.example.myapplication.databinding.ActivityGuardianScreenBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Random
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging

class GuardianScreen : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener, GoogleMap.OnMapClickListener {
    private lateinit var googleMap: GoogleMap
    private var mapInitialised = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityGuardianScreenBinding
    private val markers: MutableList<Marker> = mutableListOf()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val allPolygonsLiveData = MutableLiveData<List<List<LatLng>>>()
    private lateinit var dynamoDBClient: AmazonDynamoDBClient
    private var dynamoDBItem: DynamoDBItem = DynamoDBItem(
        user = "",
        isInside = "",
        steps = "aint legal",
        danger = "",
        respiratoryRate = "",
        heartRate = "",
        lat = "",
        long = ""
    )

    override fun onBackPressed() {
//        onMapReady(googleMap)
    }

    override fun onResume() {
        super.onResume()
        if(mapInitialised){
            googleMap?.let {
                onMapReady(it)
            }
        }
//        updateUIElementsWithUserData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardian_screen)

//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("Guardian", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//            val token = task.result
//            Log.d("Guardian", token)
//            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
//        })

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        initializeDynamoDBClient()
        updateUIElementsWithUserData()
        val createGeoFencingButton = findViewById<Button>(R.id.createGeoFencing)
        createGeoFencingButton.setOnClickListener {

            val changeToCreateGeoFencingScreen = Intent(this@GuardianScreen , GeoFencing::class.java)
            startActivity(changeToCreateGeoFencingScreen)
        }
        val checkUserSymptomsButton = findViewById<Button>(R.id.checkSysmtoms)
        checkUserSymptomsButton.setOnClickListener(){
            val changeToUserSymtomsScreen = Intent(this@GuardianScreen , UserSymptoms::class.java)
            startActivity(changeToUserSymtomsScreen)
        }


    }

    private fun initializeDynamoDBClient() {
        val awsCredentials = BasicAWSCredentials("AKIAQKCB34WRBO6ZH3C2", "kqOqBVIIlfwyQInhAvs2nVizbnejSI5ToB3H7Oxf")
        dynamoDBClient = AmazonDynamoDBClient(awsCredentials)
        dynamoDBClient.setRegion(Region.getRegion(Regions.US_EAST_1))
    }
    private fun updateUIElementsWithUserData() {
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    val dynamoDBMapper = DynamoDBMapper(dynamoDBClient)

// Create a ScanRequest to scan the entire table
                    val scanRequest = ScanRequest()
                        .withTableName("guardian_table") // Replace with your table name

// Perform the scan operation
                    val scanResult: ScanResult = dynamoDBClient.scan(scanRequest)

// Process the scan result, which contains all items in the table
                    for (item in scanResult.items) {
                        dynamoDBItem = createDynamoDBItem(item)
                        // Now you have a DynamoDBItem with the parsed values
//                        println(dynamoDBItem)
                    }
                    // Process the query result on the background thread
                    // You can update the UI by switching back to the main thread if needed
                } catch (e: Exception) {
                    // Handle exceptions
                }
            }
        }
        val dangerLevelText = findViewById<TextView>(R.id.dangerLevelText)
        dangerLevelText.text = "Danger Level : ${dynamoDBItem.danger}"
        val heartRateText = findViewById<TextView>(R.id.heartRateText)
        heartRateText.text = "Heart Rate : ${dynamoDBItem.heartRate}"
        val respiratoryRateText = findViewById<TextView>(R.id.respRateText)
        respiratoryRateText.text = "Respiratory Rate : ${dynamoDBItem.respiratoryRate}"
        val stepCountText = findViewById<TextView>(R.id.stepCountText)
        stepCountText.text = "Step Count Text : ${dynamoDBItem.steps}"
    }

    private fun fetchLatLongForPolygons() {
        coroutineScope.launch {
            try {
                val scanRequest = ScanRequest().withTableName("Polygons")
                val result = dynamoDBClient.scan(scanRequest)
                val items = result.items

                val polygons = items.mapNotNull { item ->
                    convertItemToPolygon(item)
                }

                withContext(Dispatchers.Main) {
                    allPolygonsLiveData.value = polygons
                }

                Log.d("MainActivity", "Fetched polygons $allPolygonsLiveData")

            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching from DynamoDB", e)
            }
        }
    }
    private fun convertItemToPolygon(item: Map<String, AttributeValue>): List<LatLng>? {
        val pointsList = item["Points"]?.l ?: return null
        return pointsList.mapNotNull { pointMap ->
            val latitude = pointMap.m["Latitude"]?.n?.toDoubleOrNull()
            val longitude = pointMap.m["Longitude"]?.n?.toDoubleOrNull()
            if (latitude != null && longitude != null) LatLng(latitude, longitude) else null
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        mapInitialised = true
        fetchLatLongForPolygons()
//        getUserData()
        allPolygonsLiveData.observe(this, Observer { polygons ->
            polygons.forEach { latLngList ->
                val clickablePolygonOptions = PolygonOptions().clickable(true)
                latLngList.forEach { latLong ->
                    clickablePolygonOptions.add(latLong)
                }
                val addedPolygon = this.googleMap.addPolygon(clickablePolygonOptions)
                stylePolygon(addedPolygon)
            }
        })

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(33.407134, -111.921223), 17f))

        // Set listeners for click events.
        googleMap.setOnMapClickListener(this)
        googleMap.setOnPolygonClickListener(this)
    }
    override fun onPolygonClick(polygon: Polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        var color = polygon.strokeColor xor 0x00ffffff
        polygon.strokeColor = color
        color = polygon.fillColor xor 0x00ffffff
        polygon.fillColor = color
        Toast.makeText(this, "Area type ${polygon.tag?.toString()}", Toast.LENGTH_SHORT).show()
    }
    private fun stylePolygon(polygon: Polygon) {
        // Get the data object stored with the polygon.
        val type = polygon.tag?.toString() ?: ""
        var pattern: List<PatternItem>? = null
        var strokeColor = COLOR_BLACK_ARGB
        var fillColor = COLOR_WHITE_ARGB

        pattern = PATTERN_POLYGON_BETA
        strokeColor = getRandomColor()
        fillColor = getRandomColor()
        polygon.strokePattern = pattern
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
    }
    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
    private val PATTERN_GAP_LENGTH_PX = 20
    private val DOT: PatternItem = Dot()
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())
    private val COLOR_BLACK_ARGB = -0x1000000
    private val POLYLINE_STROKE_WIDTH_PX = 12
    // Create a stroke pattern of a gap followed by a dot.
    private val PATTERN_POLYLINE_DOTTED = listOf(GAP, DOT)
    private val COLOR_WHITE_ARGB = -0x1
    private val COLOR_DARK_GREEN_ARGB = -0xc771c4
    private val COLOR_LIGHT_GREEN_ARGB = -0x7e387c
    private val COLOR_DARK_ORANGE_ARGB = -0xa80e9
    private val COLOR_LIGHT_ORANGE_ARGB = -0x657db
    private val POLYGON_STROKE_WIDTH_PX = 8
    private val PATTERN_DASH_LENGTH_PX = 20

    private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX.toFloat())

    // Create a stroke pattern of a gap followed by a dash.
    private val PATTERN_POLYGON_ALPHA = listOf(GAP, DASH)

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private val PATTERN_POLYGON_BETA = listOf(DOT, GAP, DASH, GAP)
    override fun onMapClick(latLng: LatLng) {
        // Remove existing markers before adding a new one
        clearMarkers()

        // Handle the map click
        markLocationOnMap(latLng)
    }

    private fun markLocationOnMap(latLng: LatLng) {
        // Add a marker to the clicked location
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title("Grandpa Location")

        val marker = googleMap.addMarker(markerOptions)
        marker?.showInfoWindow()
        // Add the new marker to the list
        marker?.let { markers.add(it) }
    }

    private fun clearMarkers() {
        // Remove all markers from the map and clear the list
        for (marker in markers) {
            marker.remove()
        }
        markers.clear()
    }

    override fun onPolylineClick(p0: Polyline) {
        TODO("Not yet implemented")
    }
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_guardian_screen)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}
@DynamoDBTable(tableName = "guardian_table")
data class DynamoDBItem(
    val user: String,
    val isInside: String,
    val steps: String,
    val danger: String,
    val respiratoryRate: String,
    val heartRate: String,
    val lat: String,
    val long: String
)
fun createDynamoDBItem(item: Map<String, AttributeValue>): DynamoDBItem {
    val user = item["user"]?.s ?: ""
    val isInside = item["isinside"]?.s ?: ""
    val steps = item["spm"]?.s ?: ""
    val danger = item["danger"]?.s ?: ""
    val respiratoryRate = item["respiratory_rate"]?.s ?: ""
    val heartRate = item["heart_rate"]?.s ?: ""
    val lat = item["lat"]?.s ?: ""
    val long = item["lon"]?.s ?: ""

    return DynamoDBItem(
        user,
        isInside,
        steps,
        danger,
        respiratoryRate,
        heartRate,
        lat,
        long
    )
}