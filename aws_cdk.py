import os
import boto3
from aws_cdk import core
from aws_cdk import aws_sqs as sqs
from aws_cdk import aws_sns as sns
from aws_cdk import aws_dynamodb as dynamodb
from aws_cdk import aws_lambda as _lambda

class MyCDKStack(core.Stack):

    def __init__(self, scope: core.Construct, id: str, **kwargs) -> None:
        super().__init__(scope, id, **kwargs)

        # Set AWS credentials
        aws_access_key_id = 'AKIAQKCB34WRBO6ZH3C2'
        aws_secret_access_key = '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'
        aws_region = 'us-east-1'


        os.environ['AWS_ACCESS_KEY_ID'] = aws_access_key_id
        os.environ['AWS_SECRET_ACCESS_KEY'] = aws_secret_access_key
        os.environ['AWS_REGION'] = aws_region

        # 1. Create an SQS queue
        mc_project5_queue = sqs.Queue(
            self, "MC_project5",
            queue_name="MC_project5"
        )

        # 2. Create an SNS topic
        mc_project5_topic = sns.Topic(
            self, "MC_Project5_email",
            display_name="MC_Project5_email"
        )

        # 3. Create DynamoDB tables
        guardian_table = dynamodb.Table(
            self, "guardian_table",
            table_name="guardian_table",
            partition_key=dynamodb.Attribute(
                name="id",
                type=dynamodb.AttributeType.STRING
            )
        )

        polygons_table = dynamodb.Table(
            self, "Polygons",
            table_name="Polygons",
            partition_key=dynamodb.Attribute(
                name="id",
                type=dynamodb.AttributeType.STRING
            )
        )

        user_symptoms_table = dynamodb.Table(
            self, "user_symptoms",
            table_name="user_symptoms",
            partition_key=dynamodb.Attribute(
                name="id",
                type=dynamodb.AttributeType.STRING
            )
        )

        # 4. Create a Lambda function
        mc_project5_lambda = _lambda.Function(
            self, "MC_project5",
            runtime=_lambda.Runtime.PYTHON_3_9,
            handler="lambda_function.handler",
            code=_lambda.Code.from_asset("lambda"),
            environment={
                "SQS_QUEUE_URL": mc_project5_queue.queue_url,
                "SNS_TOPIC_ARN": mc_project5_topic.topic_arn,
                "GUARDIAN_TABLE_NAME": guardian_table.table_name,
                "POLYGONS_TABLE_NAME": polygons_table.table_name,
                "USER_SYMPTOMS_TABLE_NAME": user_symptoms_table.table_name,
            }
        )

        # Grant permissions for Lambda to access resources
        mc_project5_queue.grant_send_messages(mc_project5_lambda)
        mc_project5_topic.grant_publish(mc_project5_lambda)
        guardian_table.grant_read_write_data(mc_project5_lambda)
        polygons_table.grant_read_write_data(mc_project5_lambda)
        user_symptoms_table.grant_read_write_data(mc_project5_lambda)

app = core.App()
MyCDKStack(app, "MyCDKStack")
app.synth()
