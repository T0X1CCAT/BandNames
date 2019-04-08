package com.redletra.bandnames;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Handler for requests to Lambda function.
 */
public class SaveBandLambda implements RequestHandler<APIGatewayProxyRequestEvent, Object> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent event, final Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        LambdaLogger logger = context.getLogger();
        try {
            final ObjectNode node = new ObjectMapper().readValue(event.getBody(), ObjectNode.class);

            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTHEAST_2)
                    .build();
            DynamoDB dynamoDB = new DynamoDB(client);

            Table table = dynamoDB.getTable("BandName");
            JsonNode bandName = node.get("bandName");
            logger.log("bandName " + bandName.textValue());
            Item item = new Item().withPrimaryKey("bandName",bandName.textValue() ).with("name", bandName.textValue());
            table.putItem(item);
            response.setStatusCode(200);
            response.setBody("{\"response\": \"SUCCESS\"}");
        }catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("{\"response\": \"FAIL\"}");
            System.err.println("Create items failed.");
            System.err.println(e.getMessage());

        }

        return response;

    }
}
