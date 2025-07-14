package com.mindthekid.geo.cqrs.commands;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindthekid.cqrs.shared.models.UserLocation;
import com.mindthekid.cqrs.shared.dto.ApiResponse;
import com.mindthekid.cqrs.shared.services.DynamoDBService;

import java.util.List;
import java.util.Map;

public class BulkDeleteEntryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final DynamoDBService dynamoDBService;
    private final ObjectMapper objectMapper;
    
    public BulkDeleteEntryHandler() {
        this.dynamoDBService = new DynamoDBService();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            Map<String, String> pathParameters = request.getPathParameters();
            Map<String, String> queryParameters = request.getQueryStringParameters();
            
            if (pathParameters == null) {
                return createErrorResponse("user_id is required in path", 400);
            }
            
            String userId = pathParameters.get("userId");
            if (userId == null || userId.trim().isEmpty()) {
                return createErrorResponse("user_id is required in path", 400);
            }
            
            List<UserLocation> deletedLocations;
            
            // Check if we're deleting all entries or by time range
            if (queryParameters != null && 
                (queryParameters.containsKey("startTime") || queryParameters.containsKey("endTime"))) {
                
                String startTime = queryParameters.get("startTime");
                String endTime = queryParameters.get("endTime");
                
                if (startTime != null && endTime != null) {
                    // Delete by time range
                    deletedLocations = dynamoDBService.deleteUserLocationsByTimeRange(userId, startTime, endTime);
                } else if (startTime != null) {
                    // Delete from start time onwards
                    deletedLocations = dynamoDBService.deleteUserLocationsFromTime(userId, startTime);
                } else if (endTime != null) {
                    // Delete up to end time
                    deletedLocations = dynamoDBService.deleteUserLocationsUpToTime(userId, endTime);
                } else {
                    return createErrorResponse("Invalid time range parameters", 400);
                }
            } else {
                // Delete all entries for the user
                deletedLocations = dynamoDBService.deleteAllUserLocations(userId);
            }
            
            // Return success response
            Map<String, Object> responseData = Map.of(
                "deletedCount", deletedLocations.size(),
                "deletedLocations", deletedLocations,
                "message", "Successfully deleted " + deletedLocations.size() + " user locations"
            );
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Successfully deleted " + deletedLocations.size() + " user locations", 
                responseData
            );
            
            String responseBody = objectMapper.writeValueAsString(response);
            
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(responseBody)
                    .withHeaders(Map.of("Content-Type", "application/json"));
                    
        } catch (Exception e) {
            context.getLogger().log("Error bulk deleting user locations: " + e.getMessage());
            return createErrorResponse("Internal server error: " + e.getMessage(), 500);
        }
    }
    
    private APIGatewayProxyResponseEvent createErrorResponse(String message, int statusCode) {
        try {
            ApiResponse<String> errorResponse = ApiResponse.error(message);
            String responseBody = objectMapper.writeValueAsString(errorResponse);
            
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withBody(responseBody)
                    .withHeaders(Map.of("Content-Type", "application/json"));
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("{\"success\":false,\"message\":\"Error serializing response\"}")
                    .withHeaders(Map.of("Content-Type", "application/json"));
        }
    }
} 