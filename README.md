# UserSafetyMonitor
mobile computing project of group 26

## Overview
The "User Safety Monitor" is a mobile health and safety application designed to provide real-time monitoring of vulnerable individuals, such as the elderly and children. The core functionality centers on location tracking with health data integration, offering caregivers immediate updates on the well-being and whereabouts of their dependents. It employs GPS for location tracking, geo-fencing for safety zone adherence, and real-time data communication to alert on health irregularities.

## My Contribution
My contribution to the project involved developing the health monitoring component. I implemented an asynchronous task to process video recordings from the mobile device's camera to estimate heart rates. I also programmed the system to process accelerometer data to calculate respiratory rates. These health metrics were then integrated into a fuzzy logic controller developed using jFuzzyLite library, that I designed to evaluate the user's danger level, considering various inputs including heart rate, respiratory rate, and step count. This component is crucial for the system's ability to provide a comprehensive health status, enhancing the overall safety monitoring capabilities of the app.

## Instructions of how to execute the project
- Download the zip file and unzip to a folder.
- Open the app in Android Studio.
- Connect an android device and run the application.
- Click "Record" button to record a video by placing your tip of the finger close to the back camera (to stop the recording, click record again). This updates the heart rate.
- Perform movements (walking or running) to updated the Respiratory Rate.
- For every one minute, we run the Fuzzy Logic to get a Danger level, which is displayed as a Toast in the app main window.

## File structure
- MainActivity.kt
- RespiratoryRateListener.kt - Calculates Respiratory Rate per minute
- SlowTask.kt - An asynchronous task to calculate Heart Rate
- FuzzyLogicController.java - Fuzzy Logic Controller using JFuzzyLite
### Dependencies
- VideoRecorder.kt - Implemented as another component to record the video