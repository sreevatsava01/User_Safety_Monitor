package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode
import java.time.Instant


class DirectionsScreen  : AppCompatActivity() {

    val gson: Gson = Gson()
    val apiKey: String = "AIzaSyBMo52_SpqzS2jXNVyLNoVf3qUaP3b0WLk"
//    val apiKey: String = "AAIzaSyBMo52_SpqzS2jXNVyLNoVf3qUaP3b0asdd"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        System.out.println("Testing My app")
        val context = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()




        println("Yo message")
        val apiresultTextbox = findViewById<TextView>(R.id.resultOfAPI)
        val callAPIbutton = findViewById<Button>(R.id.apiCall)
        callAPIbutton.setOnClickListener() {
            val toLocationLatitude = findViewById<TextView>(R.id.Tolatitude).text
            val toLocationLongitude = findViewById<TextView>(R.id.Tolongitude).text
            val fromLocationLatitude = findViewById<TextView>(R.id.Fromlatitude).text
            val fromLocationLongitude = findViewById<TextView>(R.id.Fromlongitude).text
            if(toLocationLatitude.isBlank() || toLocationLatitude.isEmpty()
                || toLocationLongitude.isBlank()||toLocationLongitude.isEmpty()
                || fromLocationLatitude.isBlank() || fromLocationLatitude.isEmpty()
                || fromLocationLongitude.isBlank()|| fromLocationLongitude.isEmpty()){
                Toast.makeText(this@DirectionsScreen, " Enter Longitudes and Latitudes " , Toast.LENGTH_LONG).show()
            }
            else {
                val destinationForAPI: String = "$toLocationLatitude,$toLocationLongitude"
                val originForAPI: String = "$fromLocationLatitude,$fromLocationLongitude"

                try {
                    val resultsFromAPI = DirectionsApi.newRequest(context)
                        .origin(destinationForAPI)
                        .destination(originForAPI)
                        .mode(TravelMode.DRIVING)
                        .departureTime(Instant.now()).await()
//                    println(gson.toJson(resultsFromAPI))
                    resultsFromAPI?.routes?.forEach { route ->
                        route.legs.forEach { leg ->
                            val duration = leg.duration.inSeconds
                            val durationInTraffic = leg.durationInTraffic?.inSeconds ?: duration
                            val distance = leg.distance.inMeters
                            val avgSpeed = distance.toFloat() / duration
                            val currSpeed = distance.toFloat() / durationInTraffic
                            println("avg speed = " + avgSpeed + " current speed = " + currSpeed)
                            if (durationInTraffic > duration) {
                                apiresultTextbox.text =
                                    getString(R.string.road_condition_poor_cognitive_workload_hcw)
                            } else {
                                apiresultTextbox.clearComposingText()
                                apiresultTextbox.text =
                                    getString(R.string.road_condition_good_cognitive_workload_lcw)
                            }
                        }


                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}