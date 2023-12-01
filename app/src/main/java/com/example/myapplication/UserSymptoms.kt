package com.example.myapplication
import android.os.Bundle
import android.widget.RatingBar
import androidx.activity.ComponentActivity
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserSymptoms : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_symptoms)

    }



}
