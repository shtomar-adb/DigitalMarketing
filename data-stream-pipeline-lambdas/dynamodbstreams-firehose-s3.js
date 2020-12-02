'use strict';
2	
3	const AWS = require('aws-sdk');
4	var parse = AWS.DynamoDB.Converter.output;
5	const firehose = new AWS.Firehose({ region: 'us-west-2' });
6	
7	exports.handler = (event, context, callback) => {
8	    
9	    console.log("------ Received event" + JSON.stringify(event));
10	    var fireHoseInput = [];
11	    
12	    event.Records.forEach((record) => {
13	
14	        console.log(record);
15	        
16	        if ((record.eventName == "INSERT")||(record.eventName == "MODIFY")) {
17	            fireHoseInput.push({ Data: JSON.stringify(parse({ "M": record.dynamodb.NewImage })) });
18	        }
19	    });
20	
21	    var params = {
22	        DeliveryStreamName: 'user_response',
23	        Records: fireHoseInput
24	    };
25	    if(fireHoseInput.length != 0)
26	    {
27	    firehose.putRecordBatch(params, function (err, data) {
28	        if (err) console.log(err, err.stack); // an error occurred
29	        else console.log(data);           // successful response
30	    });
31	    }
32	    else
33	        {
34	            console.log("No data to transmit");
35	        }
36	    callback(null, `Successfully processed records.`);
37	};