package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import androidx.activity.ComponentActivity


class Symptoms : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val uploadSignsButton = findViewById<Button>(R.id.uploadSigns);
        uploadSignsButton.setOnClickListener(){
            var heartRate = intent.getIntExtra("HRmeasurements" ,0)
            var respiratoryRate = intent.getIntExtra("RRmeasurements" ,0)
            var hrbool = intent.getBooleanExtra("HRbool", false)
            var rrbool = intent.getBooleanExtra("RRbool", false)
            val symptomsMap: MutableMap<String, Float> = HashMap()
            if(hrbool){
                symptomsMap["heart_rate"] = heartRate.toFloat()
            }
            if(rrbool){
                symptomsMap["repiratory_rate"] = respiratoryRate.toFloat()
            }

        }
    }
}

