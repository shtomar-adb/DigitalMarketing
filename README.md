# DigitalMarketing

## University Name: http://www.sjsu.edu/ 
## Course: Cloud Technologies
## Professor Sanjay Garje (This is link to LinkedIn Profile)
## Student: 
## Anupama Kurudi -
## Gunjan Srivastava -
## Babu Rajendran -
## Shivam Tomar
## Project Introduction:

Create an Android App to capture relevant user data. Using this data, relevant campaigns are run, to have more reach on the targeted users. Users respond to the campaigns and their responses are saved, on which data analytics is performed to understand how the campaign faired. This complete picture allows companies running their digital marketing campaigns to have a better understanding on how they can improve their campaigns.

## Problem 
Organizations invest a big part of their budget on advertising and marketing. In digital marketing, organizations often fail to know how their campaigns faired. The campaigns they send out to the users might be a hit or miss. 

## Solution 
DIGITAL MARKETING with ANALYTICS - to targeted audience and capturing user responses.
Here, with the help of the information the user has entered, generate more relevant campaigns for them. Also, by capturing their responses - "Interested" or "Not Interested", help the organization do better advertising and marketing. 

## Technologies Used
 
"	Android SDK
"	Firebase
"	Amazon Cognito
"	Amazon DynamoDB
"	AWS Elastic Beanstalk
"	Amazon RDS
"	Amazon Cloudwatch
"	Amazon SNS
"	API Gateway
"	Lambda Functions
"	Kinesis Firehose
"	S3
"	Quicksight


## Feature List
1.	Android App to capture user data
2.	Admin Portal to run Campaigns
3.	Push Notifications to capture user reaction
4.	Perform Analytics on the collected data

## Pre-requisites Set Up
## AWS resources required
" AWS Account
"	Android SDK
"	Firebase
"	Amazon Cognito
"	Amazon DynamoDB
"	AWS Elastic Beanstalk
"	Amazon RDS
"	Amazon Cloudwatch
"	Amazon SNS
"	API Gateway
"	Lambda Functions
"	Kinesis Firehose
"	S3
"	Quicksight



## Android App

## Code implementation

## Push Notification

1. Query the DB with Campaign_id & Segment_id
2. Get or Create Topic
3. Iterate through all Device Tokens and get EndpointARNs
4. Clear the Subscribers of the Topic
5. Subscribe the Device EndpointARNs to the Topic generated
6. Generate the Push Notification:
Campaign name: Title
Campaign content: Message Body
7. Publish the Notification to the Topic generated earlier
8. Log the Campaign Details to keep the record of the Campaigns Run


## Analytics

### Code implementation
The code for Analytics data streams is present in the directory [data-stream-pipeline-lambdas](./data-stream-pipeline-lambdas).
It contains two files
  * [user_reponse_handler.js](./data-stream-pipeline-lambdas/user_reponse_handler.js): Contains the Lambda function implementation for the Lambda that get triggered by API Gateway. The API Gateway has a REST endpoint, where mobile apps sends the user response to campaigns. Ths API Gateway passes the request body to Lambda function, which further saves it to the dynamo DB.
 * [dynamodbstreams-firehose-s3.js](./data-stream-pipeline-lambdas/dynamodbstreams-firehose-s3.js): Contains the Lambda function implementation for the Lambda that get triggered by dynamo DB streams, which are generated when a new user response is saved in dynamo DB. This Lambda function ingest user response data into Kinesis Firehose, which further is saved into S3 based data lake.

