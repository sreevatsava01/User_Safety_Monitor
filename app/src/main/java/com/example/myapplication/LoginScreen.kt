package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.myapplication.databinding.ActivityLoginScreenBinding
import com.google.firebase.Firebase
import com.google.firebase.database.database

class LoginScreen : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                    val steps = event.values[0].toInt()
                    // Update the UI with the current step count
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle sensor accuracy changes if needed
            }
        }
        if (stepCounterSensor != null) {
            sensorManager.registerListener(
                stepListener,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            System.out.println("Sensor Present")
        } else {
            System.out.println("Sensor not Present")
            // Handle the absence of a step counter sensor
        }
        val signInbutton = findViewById<Button>(R.id.signInButton)
        signInbutton.setOnClickListener(){

            val userNameEditText = findViewById<TextView>(R.id.emailEditText)
            val userName = userNameEditText.text.toString()
            val passwordEditText = findViewById<TextView>(R.id.passwordEditText)
            val password = passwordEditText.text.toString()
            if(userName.isBlank() || password.isBlank()) {
                Toast.makeText(
                    this@LoginScreen,
                    "Enter Correct credentials",
                    Toast.LENGTH_SHORT
                ).show()
            }
//            TODO ensure correct password while after inital development
            if(userName.equals("grandpa") && password.equals("100YrsOld")){
                val switchToGrandpaActivity = Intent(this@LoginScreen , MainActivity::class.java)
                startActivity(switchToGrandpaActivity)
//                finish()
            }
            if(userName.equals("guardian") && password.equals("angel")){
                val switchToGrandpaActivity = Intent(this@LoginScreen , GuardianScreen::class.java)
                startActivity(switchToGrandpaActivity)
//                finish()
            }
        }

//        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_login_screen)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }


}