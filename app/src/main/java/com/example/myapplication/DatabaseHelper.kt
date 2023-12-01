package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.sql.Timestamp
import java.util.Date

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "symtomsDatabase.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create your database tables here
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS symtoms (" +
                    "pkey text primary key," +
                    "heart_rate FLOAT," +
                    "repiratory_rate FLOAT," +
                    "nausea numeric check(fever >= 0.0 and fever <= 5.0)," +
                    "headAche numeric check(headAche >= 0.0 and headAche <= 5.0)," +
                    "diarrhea numeric check(diarrhea >= 0.0 and diarrhea <= 5.0)," +
                    "soar_throat numeric check(soar_throat >= 0.0 and soar_throat <= 5.0)," +
                    "fever numeric check(fever >= 0.0 and fever <= 5.0)," +
                    "muscle_ache numeric check(muscle_ache >= 0.0 and muscle_ache <= 5.0)," +
                    "loss_of_smell_taste numeric check(loss_of_smell_taste >= 0.0 and loss_of_smell_taste <= 5.0)," +
                    "cough numeric check(cough >= 0.0 and cough <= 5.0)," +
                    "shortness_breath numeric check(shortness_breath >= 0.0 and shortness_breath <= 5.0)," +
                    "feeling_tired numeric check(feeling_tired >= 0.0 and feeling_tired <= 5.0)" +
                    ");"
        )

        // Create a new table for polygons
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS polygons (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "points TEXT" +  // Column to store polygon points as a JSON string
                    ");"
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }




    fun uploadSymptoms(symptomsData: Map<String, Float>) {
        val db = this.writableDatabase
        val values = ContentValues()
        val date = Date()
        val timestamp = Timestamp(date.time)
        values.put("pkey", timestamp.toString())

        if (symptomsData != null) {
            for (s in symptomsData.keys) {
                values.put(s, symptomsData.get(s))
            }
        }
//        values.put("heart_rate", symptomsData["heart_rate"])
//        values.put("repiratory_rate", symptomsData["repiratory_rate"])
//        values.put("nausea", symptomsData["nausea"])
//        values.put("headAche", symptomsData["headAche"])
//        values.put("diarrhea", symptomsData["diarrhea"])
//        values.put("soar_throat", symptomsData["soar_throat"])
//        values.put("fever", symptomsData["fever"])
//        values.put("muscle_ache", symptomsData["muscle_ache"])
//        values.put("loss_of_smell_taste", symptomsData["loss_of_smell_taste"])
//        values.put("cough", symptomsData["cough"])
//        values.put("shortness_breath", symptomsData["shortness_breath"])
//        values.put("feeling_tired", symptomsData["feeling_tired"])
        db.insert("symtoms", null, values)
        db.close()
    }


}
