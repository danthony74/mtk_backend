package com.mindthekid.geo.cqrs.commands;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindthekid.models.UserLocation;
import com.mindthekid.cqrs.shared.dto.ApiResponse;
import com.mindthekid.cqrs.shared.services.DynamoDBService;

import java.util.Map;

public class DeleteEntryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final DynamoDBService dynamoDBService;
    private final ObjectMapper objectMapper;
    
    public DeleteEntryHandler() {
        this.dynamoDBService = new DynamoDBService();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            Map<String, String> pathParameters = request.getPathParameters();
            
            if (pathParameters == null) {
                return createErrorResponse("user_id is required in path", 400);
            }
            
            String userId = pathParameters.get("userId");
            if (userId == null || userId.trim().isEmpty()) {
                return createErrorResponse("user_id is required in path", 400);
            }
            
            String dateTime = pathParameters.get("dateTime");
            if (dateTime == null || dateTime.trim().isEmpty()) {
                return createErrorResponse("date_time is required in path", 400);
            }
            
            // Check if the entry exists before deleting
            UserLocation existingLocation = dynamoDBService.getUserLocation(userId, dateTime);
            if (existingLocation == null) {
                return createErrorResponse("User location not found", 404);
            }
            
            // Delete the user location
            dynamoDBService.deleteUserLocation(userId, dateTime);
            
            // Return success response
            Map<String, Object> responseData = Map.of(
                "deletedLocation", existingLocation,
                "message", "User location deleted successfully"
            );
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "User location deleted successfully", 
                responseData
            );
            
            String responseBody = objectMapper.writeValueAsString(response);
            
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(responseBody)
                    .withHeaders(Map.of("Content-Type", "application/json"));
                    
        } catch (Exception e) {
            context.getLogger().log("Error deleting user location: " + e.getMessage());
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