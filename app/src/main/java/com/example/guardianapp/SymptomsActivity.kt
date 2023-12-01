package com.example.guardianapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.amazonaws.services.dynamodbv2.model.AttributeValue

class SymptomsActivity : AppCompatActivity() {

    private lateinit var tvSymptoms: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptoms)

        tvSymptoms = findViewById(R.id.tvSymptoms)
        val symptomsDBManager = SymptomsDBManager(this)
        symptomsDBManager.fetchLastSymptomsEntry { symptoms ->
            displaySymptoms(symptoms)
        }
    }

    private fun displaySymptoms(symptoms: Map<String, AttributeValue>) {
        // This is a simple implementation. Modify as needed for your specific use case.
        val symptomsText = symptoms.map { (key, value) -> "$key: ${value.n}" }
            .joinToString("\n")
        tvSymptoms.text = symptomsText
    }
}
