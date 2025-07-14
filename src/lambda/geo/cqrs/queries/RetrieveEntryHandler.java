package com.mindthekid.geo.cqrs.queries;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindthekid.models.UserLocation;
import com.mindthekid.geo.cqrs.shared.services.DynamoDBService;
import com.mindthekid.geo.cqrs.shared.dto.ApiResponse;

import java.util.Map;

public class RetrieveEntryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final DynamoDBService dynamoDBService;
    private final ObjectMapper objectMapper;
    
    public RetrieveEntryHandler() {
        this.dynamoDBService = new DynamoDBService();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            Map<String, String> pathParameters = request.getPathParameters();
            Map<String, String> queryParameters = request.getQueryStringParameters();
            Map<String, Object> authorizerContext = request.getRequestContext().getAuthorizer();
            
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
            
            // Get the requesting user ID from authorizer context
            String requestingUserId = null;
            if (authorizerContext != null) {
                requestingUserId = (String) authorizerContext.get("userId");
            }
            
            if (requestingUserId == null || requestingUserId.trim().isEmpty()) {
                return createErrorResponse("Requesting user ID is required", 401);
            }
            
            // Check if the requesting user is authorized to access this data
            if (!requestingUserId.equals(userId)) {
                return createErrorResponse("Unauthorized access to user location data", 403);
            }
            
            // Retrieve the specific user location
            UserLocation userLocation = dynamoDBService.getUserLocation(userId, dateTime);
            
            if (userLocation == null) {
                return createErrorResponse("User location not found", 404);
            }
            
            // Return success response
            ApiResponse<UserLocation> response = ApiResponse.success("User location retrieved successfully", userLocation);
            String responseBody = objectMapper.writeValueAsString(response);
            
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(responseBody)
                    .withHeaders(Map.of("Content-Type", "application/json"));
                    
        } catch (Exception e) {
            context.getLogger().log("Error retrieving user location: " + e.getMessage());
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