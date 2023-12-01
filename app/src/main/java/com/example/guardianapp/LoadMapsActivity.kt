package com.example.guardianapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.example.guardianapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.PolygonOptions

class LoadMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(33.407134, -111.921223), 18f))
        val dynamoDBManager = DynamoDBManager(this)
        dynamoDBManager.fetchAllPolygons({ polygons ->
            addPolygonsToMap(polygons, mMap)
        }, { error ->
            // Handle error
        })

    }

    fun addPolygonsToMap(polygons: List<List<LatLng>>, googleMap: GoogleMap) {
        polygons.forEach { polygonPoints ->
            val polygonOptions = PolygonOptions().addAll(polygonPoints).strokeColor(Color.RED).fillColor(Color.BLUE)
            googleMap.addPolygon(polygonOptions)
        }
    }
}