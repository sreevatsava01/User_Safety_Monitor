# User_Safety_Monitor

##### Mobile computing project of group 26

Our project consists of creating an android application which enable a care taker(guardian) to get the health information on the fly and be able to check on the User(grandpa) This enables the care taker to take necessary action and keep tabs on the user so that their health is always at the best condition.

Link to our github repo : [Link](https://github.com/sreevatsava01/User_Safety_Monitor/tree/main)

### Prerequisites:

- Android Studio
- Google Maps API key
- Android device or emulator with Google Play services
- Minimum SDK version: (specify your minimum SDK)

### Setup:

- The Google Maps API key is already provided. If need obtain a Google Maps API key from the Google Cloud Console and place the API key in the AndroidManifet.xml file.
- Ensure all dependencies are correctly installed. They should be listed in the build.gradle files.
- The AWS Credentials for DynamoDB is already provided. If need replace with the Credentials in the DynamoDBManager file.

### Running the App:

- Connect your Android device and enable USB debugging, or set up an emulator in Android Studio.
- In Android Studio, click on 'Run' (play button) and select the device or emulator. This will build the app and the app will open on your device/emulator.
- Ensure location services are enabled on the device for full functionality.

### Permissions:

- Coarse location access
- Fine location access
- Background location access

### Testing:

- The default geofence of different polygon shapes are created based on the following coordinates provided:

```
Polygon 1[ (33.408210082137195, -111.91948313266039),
                    (33.408210082137195, -111.91842835396528),
                    (33.40668809164925, -111.91825702786446),
                    (33.406563823502914, -111.91950559616089) ]
Polygon 2 [ (33.407622616000666, -111.9177108630538),
            	        (33.40621396853258, -111.91765051335096),
                     (33.406253992012566, -111.91558990627527),
                     (33.407550127050044, -111.91550608724356) ]
```

- The current location of the device is set to gps coordinates `(33.4072, -111.9199)` for testing purposes.
- Using the emulator setting to start a route from start coordinates `(33.4072, -111.92) `to destination coordinates `(33.4078, -11.913)`
- As the location moves through geofence, the app gets notifications/toast of geofence entry/exit.

