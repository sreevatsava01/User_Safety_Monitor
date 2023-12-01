# User Safety Monitor Android App

## Overview
The User Safety Monitor Android App is designed to enhance the safety and well-being of vulnerable individuals, particularly the elderly, by providing real-time monitoring and health data management. This README provides instructions on how to run the Android app and use its key features.
mobile computing project of group 26
# Project Name README

## AWS Resource Creation using AWS CDK

### Prerequisites

Before you begin, make sure you have the following set up:

1. AWS CLI installed and configured with your AWS account credentials.

2. AWS CDK installed. If not, you can install it using pip:

   ```bash
   pip install aws-cdk-lib
Create AWS Resources
Follow these steps to create the specified AWS resources using AWS CDK:

Clone or download this repository.

Navigate to the AWS CDK app directory:
 ```bash
    cd aws-cdk-app
```

3. Open the aws_cdk.py file and replace the placeholders with your AWS credentials:
# Set AWS credentials
```python
aws_access_key_id = 'YOUR_ACCESS_KEY_ID'
aws_secret_access_key = 'YOUR_SECRET_ACCESS_KEY'
aws_region = 'YOUR_AWS_REGION'
```
4. Save the changes.

5. Deploy the AWS CDK stack:
   ```bash
   cdk deploy

# Data Collection

## Features
- Record video and view recorded videos.
- Calculate respiratory rate and heart rate from recorded videos.
- Enter and upload symptoms for health monitoring.
- Display symptoms at the Guardian's profile.

## Usage
1. **Record Video:**
   - On the app's main screen, click the "Record Video" button.
   - The app will start recording the video, which can be viewed after recording.
   - Click the "Record Video" button again to calculate respiratory rate and heart rate from the recording.

2. **Enter Symptoms:**
   - Click the "Symptoms" button on the main screen to enter health symptoms for Grandpa.
   - After entering symptoms, click the "Upload Symptoms" button to send them to the Database Manager.

3. **Guardian Profile:**
   - Click the "See Symptoms" button on the main screen to view the symptoms entered by Grandpa.
   - Symptoms will be displayed in the Guardian's profile for monitoring.
