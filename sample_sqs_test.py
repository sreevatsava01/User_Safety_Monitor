# Test code for testing only lambda integrartion. this will send an email to svmylava@asu.edu with the given data and 
# add it to the dynmoDB table 

import boto3
import json 
from decimal import Decimal

# Data to hit the required SQS queue for testing.
aws_access_key = 'AKIAQKCB34WRBO6ZH3C2'
aws_secret_key = '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'
aws_region = 'us-east-1'
queue_url = 'https://sqs.us-east-1.amazonaws.com/021612979618/MC_project5'

# Create an SQS client
sqs = boto3.client('sqs', region_name=aws_region, aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key)

data = {
    'danger': 'high',
    'isinside': 'false',
    'spm': "120",
    'lat': "33.4065506",
    "lon": "-111.9230067",
    "heart_rate": "120",
    "respiratory_rate": "30" 
}

# data_str = {key: str(value) for key, value in data.items()}
message_body = json.dumps(data)


# Send message to SQS queue
response = sqs.send_message(
    QueueUrl=queue_url,
    MessageBody=message_body
)

print(f"Message sent to SQS with MessageId: {response['MessageId']}")