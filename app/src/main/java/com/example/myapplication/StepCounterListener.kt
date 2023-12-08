package com.example.myapplication
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import java.util.LinkedList

class StepCounterListener(private val stepCountListener: StepCountListener) : SensorEventListener {
    private var initialStepCount = -1
    private var lastMinuteSteps = LinkedList<Pair<Long, Int>>() // Timestamp and step count

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            if (initialStepCount == -1) {
                initialStepCount = totalSteps
            }
            val currentStepCount = totalSteps - initialStepCount
            val currentTime = System.currentTimeMillis()

            // Add the current step count to the list
            lastMinuteSteps.addLast(Pair(currentTime, currentStepCount))

            // Remove steps older than one minute
            while (lastMinuteSteps.isNotEmpty() && currentTime - lastMinuteSteps.first.first > 60000) {
                lastMinuteSteps.removeFirst()
            }

            // The first element in the list is the step count from one minute ago
            val stepsLastMinute = if (lastMinuteSteps.isNotEmpty()) currentStepCount - lastMinuteSteps.first.second else 0

            Log.d("StepCounterListener", "Steps in the last minute: $stepsLastMinute")
            stepCountListener.onStepCountChanged(stepsLastMinute)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    fun resetStepCount() {
        initialStepCount = -1
        lastMinuteSteps.clear()
    }
}

interface StepCountListener {
    fun onStepCountChanged(stepCount: Int)
}