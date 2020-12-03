# DigitalMarketing

University Name: http://www.sjsu.edu/     Course: Cloud Technologies    Professor Sanjay Garje (This is link to LinkedIn Profile)    Student: {Please insert your name and link to LinkedIn Profile}    Project Introduction (What the application does, feature list)    Sample Demo Screenshots    Pre-requisites Set Upo   Here includes bullet point list of resources one need to configure in their cloud account. (E.g. For AWS: S3 buckets, CloudFront etc.)o   List of required software to download locally (E.g. Spring, JDK, Eclipse IDE etc.)o   Local configuration     How to set up and run project locally?o   Here include quick steps on how to compile and run your project on local machine (whichever you used, Mac or Windows either one).  




##Android App

## Code implementation

## Analytics

### Code implementation
The code for Analytics data streams is present in the directory [data-stream-pipeline-lambdas](./data-stream-pipeline-lambdas).
It contains two files
  * [user_reponse_handler.js](./data-stream-pipeline-lambdas/user_reponse_handler.js): Contains the Lambda function implementation for the Lambda that get triggered by API Gateway. The API Gateway has a REST endpoint, where mobile apps sends the user response to campaigns. Ths API Gateway passes the request body to Lambda function, which further saves it to the dynamo DB.
 * [dynamodbstreams-firehose-s3.js](./data-stream-pipeline-lambdas/dynamodbstreams-firehose-s3.js): Contains the Lambda function implementation for the Lambda that get triggered by dynamo DB streams, which are generated when a new user response is saved in dynamo DB. This Lambda function ingest user response data into Kinesis Firehose, which further is saved into S3 based data lake.

