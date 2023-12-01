package com.example.myapplication

import android.app.ActionBar
import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    var heartRate: Int = 0
    var respiratoryRate: Int = 0
    var hasmeasureHR: Boolean = false
    var hasmeasureRR: Boolean = false

    private lateinit var videoRecorder: VideoRecorder
    private lateinit var cameraFloatingWindow: PreviewView

    override fun onBackPressed() {
        //do nothign
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar: ActionBar? = actionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val sympButton = findViewById<Button>(R.id.symptoms)
        sympButton.setOnClickListener() {
            if (!hasmeasureHR || !hasmeasureRR) {
                Toast.makeText(
                    this@MainActivity,
                    "measurement were not uploaded",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val changeActivity = Intent(this@MainActivity, Symptoms::class.java)
            changeActivity.putExtra("HRmeasurements", heartRate)
            changeActivity.putExtra("RRmeasurements", respiratoryRate)
            changeActivity.putExtra("HRbool", hasmeasureHR)
            changeActivity.putExtra("RRbool", hasmeasureRR)
            startActivity(changeActivity)
        }

        val checkUserSymptomsButton = findViewById<Button>(R.id.displaySymptoms)
        checkUserSymptomsButton.setOnClickListener(){
            val changeToUserSymtomsScreen = Intent(this@MainActivity , UserSymptoms::class.java)
            startActivity(changeToUserSymtomsScreen)
        }

        // Code for recording video
        cameraFloatingWindow = findViewById(R.id.videoView)
        videoRecorder = VideoRecorder(this@MainActivity, cameraFloatingWindow)
        val recordButton: Button = findViewById(R.id.record)
        recordButton.setOnClickListener {
            videoRecorder.configureCamera()
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                videoRecorder.startVideoCapturing()
            }
        }
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}