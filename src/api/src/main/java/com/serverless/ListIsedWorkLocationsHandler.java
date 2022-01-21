package com.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.common.RequestResponseHandler;
import com.serverless.dynamodb.IsedWorkLocations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ListIsedWorkLocationsHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {
            // get all isedWorkLocations
            List<IsedWorkLocations> isedWorkLocations = new IsedWorkLocations().list();

            // send the response back
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(isedWorkLocations)
                    .setHeaders(RequestResponseHandler.createHeaders())
                    .build();
        } catch (Exception ex) {
            logger.error("Error in listing isedWorkLocations: " + ex);

            // send the error response back
            Response responseBody = new Response("Error in listing isedWorkLocations: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(RequestResponseHandler.createHeaders())
                    .build();
        }
    }
}
