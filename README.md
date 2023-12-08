# User_Safety_Monitor

##### Mobile computing project of group 26

Our project consists of creating an android application which enable a care taker(guardian) to get the health information on the fly and be able to check on the User(grandpa) This enables the care taker to take necessary action and keep tabs on the user so that their health is always at the best condition.

Link to our github repo : [Link](https://github.com/sreevatsava01/User_Safety_Monitor/tree/main)

### Installation

---

##### Requirements (for AWS based testing)

```
boto3
```

#### Installing using pip:

```
pip3 install boto3
```

### Running android app

- clone the repo from the above mentioned repo
- Unzip and open the app in Android Studio
  - Upon running the app, a simple app appears on the phone
  - Add physical activity persmissions
- Walk around to get the steps

### Running AWS components

- clone the repo from the above mentioned repo
- Unzip the folder
- Run : python3 sample_sqs_test.py
  - this will send a message to the SQS queue
=======
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
