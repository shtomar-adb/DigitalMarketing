# DigitalMarketing







##Android App

## Code implementation

## Analytics

### Code implementation
The code for Analytics data streams is present in the directory [data-stream-pipeline-lambdas](./data-stream-pipeline-lambdas).
It contains two files
  * [user_reponse_handler.js](./data-stream-pipeline-lambdas/user_reponse_handler.js): Contains the Lambda function implementation for the Lambda that get triggered by API Gateway. The API Gateway has a REST endpoint, where mobile apps sends the user response to campaigns. Ths API Gateway passes the request body to Lambda function, which further saves it to the dynamo DB.
 * [dynamodbstreams-firehose-s3.js](./data-stream-pipeline-lambdas/dynamodbstreams-firehose-s3.js): Contains the Lambda function implementation for the Lambda that get triggered by dynamo DB streams, which are generated when a new user response is saved in dynamo DB. This Lambda function ingest user response data into Kinesis Firehose, which further is saved into S3 based data lake.

