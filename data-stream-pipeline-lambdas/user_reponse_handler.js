const AWS = require('aws-sdk');
2	
3	const dynamo = new AWS.DynamoDB.DocumentClient();
4	
5	
6	exports.handler = async (event, context) => {
7	    console.log('Received event:', JSON.stringify(event));
8	
9	    let body;
10	    let statusCode = '200';
11	    const headers = {
12	        'Content-Type': 'application/json',
13	    };
14	
15	    try {
16	        switch (event.httpMethod) {
17	            case 'DELETE':
18	                body = await dynamo.delete(JSON.parse(event.body)).promise();
19	                break;
20	            case 'GET':
21	                body = await dynamo.scan({ TableName: event.queryStringParameters.TableName }).promise();
22	                break;
23	            case 'POST':
24	                body = await dynamo.put(JSON.parse(JSON.stringify(event.body))).promise();
25	                break;
26	            case 'PUT':
27	                body = await dynamo.update(JSON.parse(event.body)).promise();
28	                break;
29	            default:
30	                throw new Error(`Unsupported method "${event.httpMethod}"`);
31	        }
32	    } catch (err) {
33	        statusCode = '400';
34	        body = err.message;
35	    } finally {
36	        body = JSON.stringify(body);
37	    }
38	
39	    return {
40	        statusCode,
41	        body,
42	        headers,
43	    };
44	};