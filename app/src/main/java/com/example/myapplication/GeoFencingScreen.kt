package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.amazonaws.auth.BasicAWSCredentials
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

import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID


class GeoFencingScreen : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnPolygonClickListener,GoogleMap.OnMapClickListener {

    private lateinit var googleMap: GoogleMap
    private val markers: MutableList<Marker> = mutableListOf()
    private var isAddingMarkers = false
    private lateinit var dbHelper: DatabaseHelper
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val allPolygonsLiveData = MutableLiveData<List<List<LatLng>>>()
    private lateinit var dynamoDBClient: AmazonDynamoDBClient
    private var allPolygons: List<List<LatLng>> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo_fencing)
        dbHelper = DatabaseHelper(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fencingMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        val addAreaButton = findViewById<Button>(R.id.addPolygon)
        addAreaButton.isEnabled = false
        initializeDynamoDBClient()
        addAreaButton.setOnClickListener(){
//            showTextInputDialog()
            if(markers.size < 3){
                Toast.makeText(this,  "Add 4 markers to create an area" , Toast.LENGTH_SHORT).show()
            }
            else if(markers.size == 4 || markers.size == 3){
                createPolygon()
                clearMarkers()
                Toast.makeText(this,  "Area Added" , Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,  "Only 4 markers max" , Toast.LENGTH_SHORT).show()
            }

//            clearMarkers()
        }
        val addMarkerButton = findViewById<Button>(R.id.addMarker)
        addMarkerButton.setOnClickListener(){
            isAddingMarkers = true
            Toast.makeText(this, if (isAddingMarkers) "Tap on the map to add markers" else "Adding markers disabled", Toast.LENGTH_SHORT).show()
        }
        val removeMarkerButton = findViewById<Button>(R.id.removeMarker)
        removeMarkerButton.setOnClickListener(){
            removeMostRecentMarker()
        }
//        val removeArea = findViewById<Button>(R.id.removePolygon)
//        removeArea.setOnClickListener(){
////            removeMostRecentMarker()
//        }

    }
    private fun removeMostRecentMarker() {
//        clearMarkers()
        if (markers.isNotEmpty()) {
            val mostRecentMarker = markers.removeAt(markers.size - 1)
            mostRecentMarker.remove()
        } else {
            Toast.makeText(this, "No markers to remove", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showTextInputDialog() {
        // Create an EditText to get input
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
        }

        // Create AlertDialog using Builder
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Enter Text")
            .setView(input) // Add the EditText to AlertDialog
            .setPositiveButton("OK") { dialog, which ->
                // Handle the OK button click
                val enteredText = input.text.toString()
                // Do something with the entered text
                Toast.makeText(this, "Entered: $enteredText", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        fetchAllPolygons()
        for (polygon in allPolygons) {
            var clickablePolygonOptions = PolygonOptions().clickable(true)
            for(latLng in polygon) {
                clickablePolygonOptions.add(latLng)
            }
            val addedPolygon = this.googleMap.addPolygon(clickablePolygonOptions)
            stylePolygon(addedPolygon)
        }
//
        // Style the polygon.
        // [END maps_poly_activity_add_polygon]


        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(33.407134, -111.921223), 18f))

        // Set listeners for click events.
        googleMap.setOnMapClickListener(this)
        googleMap.setOnPolygonClickListener(this)
    }
    private fun initializeDynamoDBClient() {
        val awsCredentials = BasicAWSCredentials("AKIAQKCB34WRBO6ZH3C2", "kqOqBVIIlfwyQInhAvs2nVizbnejSI5ToB3H7Oxf")
        dynamoDBClient = AmazonDynamoDBClient(awsCredentials)
        dynamoDBClient.setRegion(Region.getRegion(Regions.US_EAST_1))
    }
    private fun fetchAllPolygons() {
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    val scanRequest = ScanRequest().withTableName("Polygons")
                    val result = dynamoDBClient.scan(scanRequest)
                    val items = result.items

                    val polygons = items.mapNotNull { item ->
                        convertItemToPolygon(item)
                    }

//                    withContext(Dispatchers.Main) {
                        allPolygons = polygons
                    println(allPolygons)

//                    }
                    Log.d("MainActivity", "Fetched polygons $allPolygons")

                } catch (e: Exception) {
                    Log.e("MainActivity", "Error fetching from DynamoDB", e)
                }
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
    override fun onPolygonClick(p0: Polygon) {
//

        p0.remove()
    }

    override fun onMapClick(latLng: LatLng) {
        // Remove existing markers before adding a new one
//        clearMarkers()
        if(markers.size == 2){
            val addAreaButton = findViewById<Button>(R.id.addPolygon)
            addAreaButton.isEnabled = true
        }
        if(markers.size == 4){
            Toast.makeText(this,  "Only 4 markers can be added" , Toast.LENGTH_SHORT).show()
            return
        }
        // Handle the map click
        if(isAddingMarkers){
            markLocationOnMap(latLng)
        }
        else{
            Toast.makeText(this, if (isAddingMarkers) "Tap on the map to add markers" else "Adding markers disabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun markLocationOnMap(latLng: LatLng) {
        // Add a marker to the clicked location
        val markerOptions = MarkerOptions()
            .position(latLng)

        val marker = googleMap.addMarker(markerOptions)
//        googleMap.removeM
//        marker?.showInfoWindow()
        // Add the new marker to the list
        marker?.let { markers.add(it) }
    }

    private fun createPolygon(){
        if(markers.isEmpty()){
            return
        }
        val polygonOptions = PolygonOptions()
        polygonOptions.clickable(true)
        val points = mutableListOf<LatLng>()

        for (marker in markers) {
            points.add(marker.position)
            polygonOptions.add(marker.position)
        }
//        markers.clear()
        val polygon1 = this.googleMap.addPolygon(polygonOptions)
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon1.tag = "alpha"
        stylePolygon(polygon1)
        savePolygonToDatabase(points)
    }

    private fun savePolygonToDatabase(points: MutableList<LatLng>) {
        coroutineScope.launch {
            try {
                val awsCredentials = BasicAWSCredentials("AKIAQKCB34WRBO6ZH3C2", "kqOqBVIIlfwyQInhAvs2nVizbnejSI5ToB3H7Oxf")
                val client = AmazonDynamoDBClient(awsCredentials)
                client.setRegion(Region.getRegion(Regions.US_EAST_1))

                val tableName = "Polygons"
                val uniqueID = UUID.randomUUID().toString()
                val item = HashMap<String, AttributeValue>()
                item["PolygonID"] = AttributeValue().withS(uniqueID) // Replace with a unique identifier for your polygon
                item["Points"] = AttributeValue().withL(points.map { point ->
                    AttributeValue().withM(mapOf(
                        "Latitude" to AttributeValue().withN(point.latitude.toString()),
                        "Longitude" to AttributeValue().withN(point.longitude.toString())
                    ))
                })

                val putItemRequest = PutItemRequest()
                    .withTableName(tableName)
                    .withItem(item)

                client.putItem(putItemRequest)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GeoFencingScreen, "Polygon saved to DynamoDB", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error saving to DynamoDB", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GeoFencingScreen, "Error saving to DynamoDB: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun clearMarkers() {
        // Remove all markers from the map and clear the list
        for (marker in markers) {
            marker.remove()
        }
        markers.clear()
        val addAreaButton = findViewById<Button>(R.id.addPolygon)
        addAreaButton.isEnabled = false
    }
    private fun stylePolygon(polygon: Polygon) {
        // Get the data object stored with the polygon.
        val type = polygon.tag?.toString() ?: ""
        var pattern: List<PatternItem>? = null
        var strokeColor = COLOR_BLACK_ARGB
        var fillColor = COLOR_WHITE_ARGB
        pattern = PATTERN_POLYGON_ALPHA
        strokeColor = COLOR_DARK_GREEN_ARGB
        fillColor = COLOR_LIGHT_GREEN_ARGB
        polygon.strokePattern = pattern
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
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

}