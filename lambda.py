import boto3
from botocore.exceptions import ClientError
import json
from decimal import Decimal


aws_access_key = 'AKIAQKCB34WRBO6ZH3C2'
aws_secret_key = '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'
aws_region = 'us-east-1'
queue_url = 'https://sqs.us-east-1.amazonaws.com/021612979618/MC_project5'
danger_level = 'danger'
dynamodb_table_name = 'guardian_table'


sqs = boto3.client('sqs', region_name=aws_region, aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key)
sns = boto3.client('sns', region_name=aws_region, aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key)
dynamodb = boto3.resource('dynamodb', region_name=aws_region, aws_access_key_id=aws_access_key, aws_secret_access_key=aws_secret_key)


sns_topic_arn = 'arn:aws:sns:us-east-1:021612979618:MC_Project5_email'


def lambda_handler(event, context):
    try:
        for record in event['Records']:
            # Extract message body from SQS record
            message_body = record['body']

            # Parse JSON data
            json_data = json.loads(message_body)

            # Process the message
            processed_data, to_send = process_message(json_data)

            # Send processed data as an email using SES
            if(to_send):
                send_sns(processed_data)

            add_data_to_db(processed_data)
            try:
                sqs.delete_message(
                    QueueUrl=queue_url,
                    ReceiptHandle=record['receiptHandle'])
                print("Message deleted from SQS queue.")
            except ClientError as e:
                print(f"Error deleting message from SQS queue: {e}")
            

    except Exception as e:
        print(f"Error in Lambda function: {e}")
        raise e



def process_message(message_body):
    processed_data = message_body
    
    if(message_body[danger_level]=="high"):
        return processed_data, True
    else:
        return processed_data, False

def send_sns(data):
     # Convert JSON data to a string
    json_data_str = json.dumps(data, indent=2)

    try:
        response = sns.publish(
        TopicArn=sns_topic_arn,
        Message=json_data_str)
        print(f"Email sent! Message ID: {response['MessageId']}")
    except ClientError as e:
        print(f"Error sending email using SES: {e}")

def add_data_to_db(data):
    dynamodb_table = dynamodb.Table(dynamodb_table_name)
    data["user"] = "grandpa"
    dynamodb_table.put_item(Item=data)
    print("Item added to DynamoDB table!")