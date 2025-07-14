package com.mindthekid.geo.cqrs.commands;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindthekid.cqrs.shared.models.UserLocation;
import com.mindthekid.cqrs.shared.dto.ApiResponse;
import com.mindthekid.cqrs.shared.services.DynamoDBService;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BulkCreateEntryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final DynamoDBService dynamoDBService;
    private final ObjectMapper objectMapper;
    
    public BulkCreateEntryHandler() {
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
            
            // Parse as array of UserLocation objects
            UserLocation[] userLocations = objectMapper.readValue(requestBody, UserLocation[].class);
            
            if (userLocations == null || userLocations.length == 0) {
                return createErrorResponse("At least one user location is required", 400);
            }
            
            // Validate and prepare user locations
            List<UserLocation> validLocations = new ArrayList<>();
            List<String> validationErrors = new ArrayList<>();
            
            Instant now = Instant.now();
            
            for (int i = 0; i < userLocations.length; i++) {
                UserLocation location = userLocations[i];
                String validationError = validateUserLocation(location, i);
                
                if (validationError != null) {
                    validationErrors.add(validationError);
                } else {
                    // Set timestamps
                    location.setCreatedAt(now);
                    location.setUpdatedAt(now);
                    
                    // Generate lat_long index value if coordinates are provided
                    if (location.getLatitude() != null && location.getLongitude() != null) {
                        location.setLatLong(location.getLatitude() + "," + location.getLongitude());
                    }
                    
                    // Set default values if not provided
                    if (location.getIsPrivate() == null) {
                        location.setIsPrivate(false);
                    }
                    
                    if (location.getIsReal() == null) {
                        location.setIsReal(true);
                    }
                    
                    validLocations.add(location);
                }
            }
            
            if (!validationErrors.isEmpty()) {
                return createErrorResponse("Validation errors: " + String.join("; ", validationErrors), 400);
            }
            
            // Save to DynamoDB in batches
            List<UserLocation> savedLocations = dynamoDBService.bulkSaveUserLocations(validLocations);
            
            // Return success response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("createdCount", savedLocations.size());
            responseData.put("locations", savedLocations);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Successfully created " + savedLocations.size() + " user locations", 
                responseData
            );
            
            String responseBody = objectMapper.writeValueAsString(response);
            
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(201)
                    .withBody(responseBody)
                    .withHeaders(Map.of("Content-Type", "application/json"));
                    
        } catch (Exception e) {
            context.getLogger().log("Error bulk creating user locations: " + e.getMessage());
            return createErrorResponse("Internal server error: " + e.getMessage(), 500);
        }
    }
    
    private String validateUserLocation(UserLocation location, int index) {
        if (location.getUserId() == null || location.getUserId().trim().isEmpty()) {
            return "Item " + index + ": user_id is required";
        }
        
        if (location.getDateTime() == null || location.getDateTime().trim().isEmpty()) {
            return "Item " + index + ": date_time is required";
        }
        
        return null;
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