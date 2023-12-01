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
