package com.mindthekid.geo.cqrs.commands;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindthekid.cqrs.shared.models.UserLocation;
import com.mindthekid.cqrs.shared.dto.ApiResponse;
import com.mindthekid.cqrs.shared.services.DynamoDBService;

import java.time.Instant;
import java.util.Map;

public class CreateEntryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final DynamoDBService dynamoDBService;
    private final ObjectMapper objectMapper;
    
    public CreateEntryHandler() {
        this.dynamoDBService = new DynamoDBService();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            // Parse request body
            String requestBody = request.getBody();
            if (requestBody == null || requestBody.trim().isEmpty()) {
                return createErrorResponse("Request body is required", 400);
            }
            
            UserLocation userLocation = objectMapper.readValue(requestBody, UserLocation.class);
            
            // Validate required fields
            if (userLocation.getUserId() == null || userLocation.getUserId().trim().isEmpty()) {
                return createErrorResponse("user_id is required", 400);
            }
            
            if (userLocation.getDateTime() == null || userLocation.getDateTime().trim().isEmpty()) {
                return createErrorResponse("date_time is required", 400);
            }
            
            // Set timestamps
            Instant now = Instant.now();
            userLocation.setCreatedAt(now);
            userLocation.setUpdatedAt(now);
            
            // Generate lat_long index value if coordinates are provided
            if (userLocation.getLatitude() != null && userLocation.getLongitude() != null) {
                userLocation.setLatLong(userLocation.getLatitude() + "," + userLocation.getLongitude());
            }
            
            // Set default values if not provided
            if (userLocation.getIsPrivate() == null) {
                userLocation.setIsPrivate(false);
            }
            
            if (userLocation.getIsReal() == null) {
                userLocation.setIsReal(true);
            }
            
            // Save to DynamoDB
            dynamoDBService.saveUserLocation(userLocation);
            
            // Return success response
            ApiResponse<UserLocation> response = ApiResponse.success("User location created successfully", userLocation);
            String responseBody = objectMapper.writeValueAsString(response);
            
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(201)
                    .withBody(responseBody)
                    .withHeaders(Map.of("Content-Type", "application/json"));
                    
        } catch (Exception e) {
            context.getLogger().log("Error creating user location: " + e.getMessage());
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