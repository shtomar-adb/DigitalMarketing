/*******************************ANUPAMA KURUDI*********************************
 * ALL GLOBALS SECTION
 ******************************************************************************/

/************************ GLOBAL OBJECTS SECTION ******************************/
/* Initialize the AWS SDK */
var AWS = require("aws-sdk");

/* Configure the AWS SDK object */
AWS.config.update({region: "us-west-2"});

/* Create the AWS SNS object */
var sns = new AWS.SNS();

/* Create the dynamodb client to perform queries */
var dynamodb = new AWS.DynamoDB({apiVersion: "2012-08-10"});
var docClient = new AWS.DynamoDB.DocumentClient({apiVersion: '2012-08-10'});
/****************************** END SECTION ***********************************/


/************************ DB QUERY PARAMS SECTION *****************************/
/* DynamoDB table names captured in variables */
var campaignTableName = "Campaigns";
var campaignTablePrimaryKey = "campaign_id";
var segmentTableName = "Segments";
var segmentTablePrimaryKey = "segment_id";
var logTable = "CampaignRun";
var logTablePrimaryKey = "campaign_name";
/****************************** END SECTION ***********************************/


/************************** APPLICATION PLATFORM SECTION *******************************/
/* Application values captured in variables */
var platformAppName = "VirtualEmulatorDevices";
var platformAppArn = "arn:aws:sns:us-west-2:211940657833:app/GCM/VirtualEmulatorDevices";
/****************************** END SECTION ***********************************/


/********************** LOCAL DEBUG VARIABLES SECTION *************************/
// Enable each variable (set to true) to debug specific functionality
// masterDebug will turn on everything
var masterDebug = false;                              // All debugging
var mainDebug = masterDebug || true;                 // Debug msgs in the main function
var debugGetCampaignInfo = masterDebug || false;      // Debug msgs for campaign info 
var debugGetSegmentInfo = masterDebug || false;       // Debug msgs for segment info  
var debugCreateTopic = masterDebug || false;          // Debug topic creation
var debugGetEndpointARN = masterDebug || false;       // Debug the device end point creation
var debugClearSubscription = masterDebug || false;    // Debug the process of clearing subscriptions of a given topic
var debugSubscribeDevice = masterDebug || true;      // Debug the device end point subscription
var debugPublishNotification = masterDebug || false;  // Debug the process of publishing the notification
var debuglogCampaignDetails = masterDebug || true;  // Debug the process of logging the campaign and segment details

/****************************** END SECTION ***********************************/


/*********************************************************************
 * FUNCTION: getCampaignInfo
 * DESCRIPTION: Get the campaign information from the campaign ID
 * RETURN: Data object with campaign_name and campaign_id as members
 *         Access with "<var>.campaign_name and <var>.campaign_id"
 * *******************************************************************/
async function getCampaignInfo(campaign_id) {
    try {
        var params = {
                TableName : campaignTableName,
                Key: {[campaignTablePrimaryKey]: campaign_id}
        };
        if (debugGetCampaignInfo) {
            console.log("Params for querying campaign info for campaign_id: " + campaign_id);
            console.log(params);
        }
        var result = await docClient.get(params).promise();
        if (debugGetCampaignInfo) {
            console.log("Campaign query from dynamoDB SUCCESS, query 'get' result: ");
            console.log(result);
        }
        var data = {
            campaign_name: result.Item.campaign_name,
            campaign_content: result.Item.campaign_content
        };
        if (debugGetCampaignInfo) {
            console.log("Constructed return result object:");
            console.log(data);
        }
        return data;
    } catch (error) {
        if (debugGetCampaignInfo) {
            console.log("DynamoDB query failed. Error response:");
            console.error(error);
        }
        return null;
    }
}

/*********************************************************************
 * FUNCTION: getSegmentInfo
 * DESCRIPTION: Get the segment information from the segment ID
 * RETURN: Data object with segment_name and segment_devices as members
 *         Access with "<var>.segment_name and <var>.device_tokens"
 * *******************************************************************/
async function getSegmentInfo(segment_id) {
    try {
        var params = {
                TableName : segmentTableName,
                Key: {[segmentTablePrimaryKey]: segment_id}
        };
        if (debugGetSegmentInfo) {
            console.log("Params for querying segment info for segment_id: " + segment_id);
            console.log(params);
        }
        
        var result = await docClient.get(params).promise();
        if (debugGetSegmentInfo) {
            console.log("Campaign query from dynamoDB SUCCESS, query 'get' result: ");
            console.log(result);
        }
        var data = {
            segment_name: result.Item.segment_name,
            device_tokens: result.Item.devices
        };
        if (debugGetSegmentInfo) {
            console.log("Constructed return result object:");
            console.log(data);
        }
        return data;
    } catch (error) {
        if (debugGetSegmentInfo) {
            console.log("DynamoDB query failed. Error response:");
            console.error(error);
        }
        return null;
    }
}

/*********************************************************************
 * FUNCTION: getOrCreateTopic
 * DESCRIPTION: Get or create a topic with the same name as the 
 *              segment name
 * RETURN: Topic ARN if successfull and null otherwise
 * *******************************************************************/
async function getOrCreateTopic(segmentName) {
    try {
       if (debugCreateTopic) {
           console.log("Invoking SNS to create topic for segment: " + segmentName);
       }
       var result = await sns.createTopic({Name: segmentName}).promise();
       if (debugCreateTopic) {
           console.log("Successfully created topic for segment: " + segmentName);
           console.log("Result of SNS function 'createTopic':");
           console.log(result);
       }
       var topicARN = result.TopicArn;
       if (debugCreateTopic) {
           console.log("Obtained ARN for topic: " + topicARN);
       }
       return topicARN;
    } catch (error) {
        if (debugCreateTopic) {
            console.log("Topic creation using SNS API failed. Error response:");
            console.error(error);
        }
        return null;
    }
}

/*********************************************************************
 * FUNCTION:    getEndpointARN
 * DESCRIPTION: Get or create a platform application endpoint and 
 *              return the ARN for the corresponding device token
 * RETURN: Device ARN if successfull and null otherwise
 * *******************************************************************/
 async function getEndpointARN(device_token) {
    try {
        var params = {
            PlatformApplicationArn: platformAppArn,
            Token: device_token    
        };
        if (debugGetEndpointARN) {
            console.log("Invoking SNS to create platform end point with params:");
            console.log(params);
        }
        var data = await sns.createPlatformEndpoint(params).promise();
        if (debugGetEndpointARN) {
            console.log("Success in registering the device!");
        }
        return data.EndpointArn;
    } catch(error) {
        if (debugGetEndpointARN) {
            console.log("Error: End point registration failed. Error response: ");
            console.log(error);
        }
        return null;
    }
 }
 
 /*********************************************************************
 * FUNCTION:    subscribeDevice
 * DESCRIPTION: Given a topicARN and a deviceEndpointARN, subscribe 
 *              the deviceEndpointARN to the topicARN
 * RETURN: True if successful and false otherwise
 * *******************************************************************/
 async function subscribeDevice(topicARN, deviceEndpointARN) {
    try {
        var params = {
            Protocol : "application",
            TopicArn: topicARN,
            Endpoint: deviceEndpointARN
        };
        if (debugSubscribeDevice) {
            console.log("Invoking SNS to subscribe platform end point '"+ deviceEndpointARN + "' with topic ARN with params:");
            console.log(params);
        }
        var data = await sns.subscribe(params).promise();
        if (debugSubscribeDevice) {
            console.log("SUCCESS in subscribing the device. Returns object:");
            console.log(data);
        }
        return true;
    } catch(error) {
        if (debugSubscribeDevice) {
            console.log("Error: Cannot subscribe device. Error response: ");
            console.log(error);
        }
        return false;
    }
 }
 
 /*********************************************************************
 * FUNCTION: clearSubscribers
 * DESCRIPTION: Clear all subscribers to the specified topic ARN
 * RETURN: True if successfull and false otherwise
 * *******************************************************************/
 async function clearSubscribers(topicARN) {
     var params = {
         TopicArn: topicARN
     };
     if (debugClearSubscription) {
        console.log("Clearing all subscribers to the topic " + topicARN);
     }
     do {
       var data = await sns.listSubscriptionsByTopic(params).promise();
       params = {
           TopicArn : topicARN,
           NextToken: data.NextToken
       };
       var subscriptions = data.Subscriptions;
       for (var i = 0; i < subscriptions.length; ++i) {
           var sub = subscriptions[i];
           await sns.unsubscribe({SubscriptionArn: sub.SubscriptionArn}).promise()
           .then(function(data) {
               if (debugClearSubscription) {
                   console.log("Successfully removed subscription " + sub.SubscriptionArn);
               }
           })
           .catch(function(error){
               if (debugClearSubscription) {
                   console.log("Error: Cannot delete subscription " +  sub.SubscriptionArn + ". Error:");
                   console.log(error);
               }
           });
       }
     } while (data.NextToken != null);
 }
 
 /*********************************************************************
 * FUNCTION: subscribeDeviceTokensToTopic
 * DESCRIPTION: Subscribe the specified device tokens to the given
 *              topic ARN
 * RETURN: True if successfull and false otherwise
 * *******************************************************************/
 async function subscribeDeviceTokensToTopic(topicARN, device_tokens) {
     var result = true;
     for (var i = 0; i < device_tokens.length; ++i) {
         var device_token = device_tokens[i];
         var device_arn = await getEndpointARN(device_tokens[i]);
         console.log("DeviceARN: " + device_arn);
         if (device_arn == null) {
            result = false;
            break;
         }
         var subscribeSuccess = await subscribeDevice(topicARN, device_arn);
         if (subscribeSuccess == false) {
             result = false;
             break;
         }
     }
     
     return result;
 }
 
 /*********************************************************************
 * FUNCTION: publishSNSMsgToTopic
 * DESCRIPTION: Publish an SNS notification message to the given topic 
 *              ARN
 * RETURN: True if successfull and false otherwise
 * *******************************************************************/
 async function publishSNSMsgToTopic(topicARN, msgText, msgTitle, segment_name)
 {
    try {
        // Construct the message
        /*var msg = {
            "default": "Empty default value",
            "GCM": "{ \"notification\": { \"text\": \"Sample message for Android endpoints\" } }"
        };*/
        var msg = {
            "default": "Nothing", 
            //"GCM": "{ \"notification\": { \"text\": \"Sample message for Android endpoints\" } }"
           "GCM": "{ \"data\": { \"message\" : \"" + msgText + "\", \"title\" :\"" + msgTitle + "\", \"segment_name\" : \"" + segment_name + "\"},  \"priority\": \"high\"  }"
           
        
            //"GCM": "{ \"data\": { \"message\": \"Sample message for Android endpoints\" } , \"android\" : {\"priority\" : \"high\"} }"
        };
        
        var msgStr = JSON.stringify(msg);
        if (debugPublishNotification) {
            console.log("Constructed messsage: ");
            console.log(msgStr);
        }
        var params = {
            Message: msgStr, /* required */
            TopicArn: topicARN,
            MessageStructure: "json",
            Subject: msgTitle
        };
        if (debugPublishNotification) {
            console.log("Invoking SNS publish with params: ");
            console.log(params);
        }
        var data = await sns.publish(params).promise();
        // Handle promise's fulfilled/rejected states
        if (debugPublishNotification) {
            console.log(`Message ${params.Message} sent to the topic ${params.TopicArn}`);
            console.log("MessageID is " + data.MessageId);
        }
        return true;
    } catch(error) {
        if (debugPublishNotification) {
            console.log("Error occurred during SNS message publish. Error description:");
            console.log(error);
        }
        return false;
    }
 }

//  /*********************************************************************
//  * FUNCTION: logCampaignDetails
//  * DESCRIPTION: Log the Devices and the Campaign Details
//  * RETURN: True if successfull and false otherwise
//  * *******************************************************************/
 async function logCampaignDetails(campaign_name, segment_name, devices)
 {
    try {
        var params = {
                TableName : logTable,
                Key: {[logTablePrimaryKey]: campaign_name},
                Item:{
                    "campaign_name": campaign_name,
                    "segment_name":  segment_name,
                    "devices": devices
        }
        };
        if (debuglogCampaignDetails) {
            console.log("Params for logging campaign details Segment_name:" + segment_name + "campaign_name:" + campaign_name + "Devices:" + devices);
            console.log(params);
        }
        
        await docClient.put(params).promise();
        if (debuglogCampaignDetails) {
            console.log("Campaign details logging to dynamoDB SUCCESS");
        }
        
        return true;
        
    } catch(error) {
        if (debuglogCampaignDetails) {
            console.log("Error occurred during logging CAMPAIGN DETAILS.");
            console.log(error);
        }
        return false;
    }
}
 
exports.handler = async (event) => {
    // Final return result
    var res = true;
    var stCode = 200;
    var bodyStr = "Success";
   
    // Initial DEBUG info
    if (mainDebug === true) {
        console.log("******************** BEGIN LAMBDA **********************");
        console.log("Event:");
        console.log(event);
    }
    
    // Notification details are found in event
    // STEP 1: Get the segment ID and campaign ID from the event
    var segment_id = Number(event.segment_id);
    var campaign_id = Number(event.campaign_id);
    if (mainDebug === true) {
        console.log("******************** REPORT EVENT **********************");
        console.log("Segment ID: " + segment_id);
        console.log("Campaign ID: " + campaign_id);
    }
    
    //**************************************************************************
    // STEP 2: Get the campaign content and the campaign name from the DB
    //**************************************************************************
    if (mainDebug === true) {
        console.log("**************** OBTAIN CAMPAIGN DATA ******************");
    }
    let campaign_data = await getCampaignInfo(campaign_id);
    if (mainDebug === true) {
        console.log("DEBUG: Obtained campaign data for campaign id: " + campaign_id);
        console.log(campaign_data);
    }
    if (campaign_data == null) {
        bodyStr = JSON.stringify(event) + "\n";
        //bodyStr = "Error: campaign_data not found for campaign_id: " + campaign_id;
        res = false;
    }
    
    //**************************************************************************
    // STEP 3: Get the segment name and device tokens from the DB
    //**************************************************************************
    var segment_data = null;
    if (res === true) {
        if (mainDebug === true) {
            console.log("**************** OBTAIN SEGMENT DATA *******************");
        }
        segment_data = await getSegmentInfo(segment_id);
        if (mainDebug === true) {
            console.log("DEBUG: Obtained segment data for segment id: " + segment_id);
            console.log(segment_data);
        }
        if (segment_data == null) {
            bodyStr = "Error: segment_data not found for segment_id: " + segment_id;
            res = false;
        }
    }
    
    //**************************************************************************
    // STEP 4: Get or create a topic which has the same name as the segment
    //**************************************************************************
    var topicARN = null;
    if (res === true) {
        var segment_name = segment_data.segment_name;
        if (mainDebug === true) {
            console.log("**************** GET OR CREATE TOPIC *******************");
        }
        topicARN = await getOrCreateTopic(segment_name);
        if (mainDebug === true) {
            console.log("TopicARN: " + topicARN);
        }
        if (topicARN == null) {
            bodyStr = "Error: Topic name: " + segment_name + " could not be created";
            res = false;
        }
    }

    //**************************************************************************
    // STEP 5: Clear all the subscribers for the topic
    //**************************************************************************
    if (res === true) {
        if (mainDebug === true) {
            console.log("**************** CLEAR SUBSCRIBERS *********************");
        }
        await clearSubscribers(topicARN);
    }
    
    //**************************************************************************
    // STEP 6: Subscribe the device ARNs from device tokens
    //**************************************************************************
    if (res === true) {
        if (mainDebug === true) {
            console.log("************** SUBSCRIBE DEVICE TOKENS *****************");
        }
        res = await subscribeDeviceTokensToTopic(topicARN, segment_data.device_tokens);
        if (res == false) {
            bodyStr = "Error: Subscribing device tokens to topic failed";
        }
    }
    
    //**************************************************************************
    // STEP 7: Publish the notification for the topic
    //**************************************************************************
    if (res === true) {
        if (mainDebug === true) {
            console.log("***************** PUSH NOTIFICATION ********************");
        }
        res = await publishSNSMsgToTopic(topicARN, campaign_data.campaign_content, 
                                  campaign_data.campaign_name, segment_data.segment_name);
        if (res === false) {
            bodyStr = "Error: Pushing SNS notification to topic failed";
        }
    }
    
    //**************************************************************************
    // STEP 8: Create an entry in the DB for "segment ID + campaign ID" 
    //               in the data base for logging Campaign Details
    //**************************************************************************
    if (res === true) {
        if (mainDebug === true) {
            console.log("************** CAMPAIGN DETAILS *****************");
        }
        res = await logCampaignDetails(campaign_data.campaign_name, segment_data.segment_name, segment_data.device_tokens);
        if (res == false) {
            bodyStr = "Error: Logging camapign details to the DB failed";
        }
    }

    //**************************************************************************
    // FINISH AND CONSTRUCT RESPONSE
    //**************************************************************************
    if (res === false) {
        stCode = 400;
    }
    var response = {
        statusCode: stCode,
        body: JSON.stringify(bodyStr),
    };
    
    return response;
};

