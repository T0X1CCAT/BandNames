package com.redletra.bandnames;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Handler for requests to Lambda function.
 */
public class GetBandsLambda implements RequestHandler<APIGatewayProxyRequestEvent, Object> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTHEAST_2)
                    .build();
            ScanRequest scanRequest = new ScanRequest()
                    .withTableName("BandName");
            client.scan(scanRequest);
            ScanResult result = client.scan(scanRequest);
            TreeSet<String> names = new TreeSet<>();
            for (Map<String, AttributeValue> item : result.getItems()){
                names.add(item.get("name").getS());
            }
            ObjectMapper objectMapper = new ObjectMapper();

            response.setStatusCode(200);
            Map <String, String> headers = new HashMap();
            headers.put("Access-Control-Allow-Origin", "*");
            response.setHeaders(headers);
            response.setBody(objectMapper.writeValueAsString(names));
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("{\"response\": \"FAIL\"}");
            System.err.println("get items failed.");
            e.printStackTrace(System.err);
            System.err.println(e.getMessage());

        }

        return response;

    }
}
