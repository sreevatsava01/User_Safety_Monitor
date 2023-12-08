package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import androidx.activity.ComponentActivity


class Symptoms : ComponentActivity() {

<<<<<<< HEAD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
=======
    private lateinit var symptomsDBManager: SymptomsDBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        symptomsDBManager = SymptomsDBManager(this)
>>>>>>> main
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

<<<<<<< HEAD
=======
            symptomsMap["nausea"] = findViewById<RatingBar>(R.id.nausearating).rating
            symptomsMap["headAche"] = findViewById<RatingBar>(R.id.headAcheRating).rating
            symptomsMap["diarrhea"] = findViewById<RatingBar>(R.id.diarrheaRating).rating
            symptomsMap["soar_throat"] = findViewById<RatingBar>(R.id.soar_throatRating).rating
            symptomsMap["fever"] = findViewById<RatingBar>(R.id.feverRating).rating
            symptomsMap["muscle_ache"] = findViewById<RatingBar>(R.id.muscle_acheRating).rating
            symptomsMap["loss_of_smell_taste"] = findViewById<RatingBar>(R.id.loss_of_smell_tasteRating).rating
            symptomsMap["cough"] = findViewById<RatingBar>(R.id.coughRating).rating
            symptomsMap["shortness_breath"] = findViewById<RatingBar>(R.id.shortness_breathRating).rating
            symptomsMap["feeling_tired"] = findViewById<RatingBar>(R.id.feeling_tiredRating).rating

            symptomsDBManager.uploadSymptomsToDynamoDB(symptomsMap)

>>>>>>> main
        }
    }
}

