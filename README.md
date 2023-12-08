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
--------------------
### Running android app
<!-- ## Instructions of how to execute the project -->
- Download the zip file and unzip to a folder.
- Open the app in Android Studio.
- Connect an android device and run the application.
- Click "Record" button to record a video by placing your tip of the finger close to the back camera (to stop the recording, click record again). This updates the heart rate.
- Perform movements (walking or running) to updated the Respiratory Rate. This will also add step count
- For every one minute, we run the Fuzzy Logic to get a Danger level, which is displayed as a Toast in the app main window.
- For testing geo-fencing, set the geofence by logging into **guardian** UI. This will update the fence in the DB
  - Then login back into the **grandpa** UI and walk around. When moving out of the fence, it will trigger a notification service and guardian will get a mail
- For geo-fence and danger levels from fuzzy-logic, the data will be pushed to AWS SQS queue every 1 minute which will trigger the SNS for notification to the guardian
  


<!-- - Clone the repo from the above mentioned repo
- Unzip and open the app in Android Studio
  - Upon running the app, a simple app appears on the phone
  - Add physical activity persmissions
- Walk around to get the steps
- Walk around to record respiratory rate
- For heart rate, record video by placing finger against camera and click **record** button -->



### Running only AWS components

- Clone the repo from the above mentioned repo
- Unzip the folder
- Run : python3 sample_sqs_test.py
  - this will send a message to the SQS queue



