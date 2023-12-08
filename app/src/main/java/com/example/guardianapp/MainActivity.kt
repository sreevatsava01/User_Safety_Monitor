package com.example.guardianapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.guardianapp.ui.theme.GuardianAppTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnShowMap = findViewById<Button>(R.id.btnShowMap)
        btnShowMap.setOnClickListener {
            val intent = Intent(this, LoadMapsActivity::class.java)
            startActivity(intent)
        }

        val btnShowSymptoms = findViewById<Button>(R.id.btnShowSymptoms)
        btnShowSymptoms.setOnClickListener {
            val intent = Intent(this, SymptomsActivity::class.java)
            startActivity(intent)
        }
//        setContent {
//            GuardianAppTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Greeting("Android")
//                }
//            }
//        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("MainActivity", "FCM Token: $token")
        }
    }
}

