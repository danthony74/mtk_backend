package com.mindthekid.geo.cqrs.test.queries;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindthekid.models.UserLocation;
import com.mindthekid.geo.cqrs.queries.BulkRetrieveEntryHandler;
import com.mindthekid.geo.cqrs.shared.dto.ApiResponse;
import com.mindthekid.geo.cqrs.shared.services.DynamoDBService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulkRetrieveEntryHandlerTest {

    @Mock
    private Context context;
    
    @Mock
    private LambdaLogger logger;
    
    @Mock
    private DynamoDBService mockDynamoDBService;
    
    private BulkRetrieveEntryHandler handler;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        when(context.getLogger()).thenReturn(logger);
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testHandleRequest_MissingPathParameters() throws Exception {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPathParameters(null);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    // Mock behavior if needed
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(400, response.getStatusCode());
            assertTrue(response.getBody().contains("user_id is required in path"));
            assertTrue(response.getBody().contains("\"success\":false"));
        }
    }

    @Test
    void testHandleRequest_MissingRequestingUserId() throws Exception {
        // Arrange
        String userId = "user123";
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(null); // No authorizer context
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    // Mock behavior if needed
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(401, response.getStatusCode());
            assertTrue(response.getBody().contains("Requesting user ID is required"));
            assertTrue(response.getBody().contains("\"success\":false"));
        }
    }

    @Test
    void testHandleRequest_EmptyRequestingUserId() throws Exception {
        // Arrange
        String userId = "user123";
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", "");
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    // Mock behavior if needed
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(401, response.getStatusCode());
            assertTrue(response.getBody().contains("Requesting user ID is required"));
            assertTrue(response.getBody().contains("\"success\":false"));
        }
    }

    @Test
    void testHandleRequest_UnauthorizedAccess() throws Exception {
        // Arrange
        String userId = "user123";
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", "user456"); // Different user trying to access user123's data
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    // Mock behavior if needed
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(403, response.getStatusCode());
            assertTrue(response.getBody().contains("Unauthorized access to user location data"));
            assertTrue(response.getBody().contains("\"success\":false"));
        }
    }
    
    @Test
    void testHandleRequest_MissingUserId() throws Exception {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("otherParam", "value");
        request.setPathParameters(pathParams);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", "user123");
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    // Mock behavior if needed
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(400, response.getStatusCode());
            assertTrue(response.getBody().contains("user_id is required in path"));
            assertTrue(response.getBody().contains("\"success\":false"));
        }
    }
    
    @Test
    void testHandleRequest_EmptyUserId() throws Exception {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", "");
        request.setPathParameters(pathParams);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", "user123");
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    // Mock behavior if needed
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(400, response.getStatusCode());
            assertTrue(response.getBody().contains("user_id is required in path"));
            assertTrue(response.getBody().contains("\"success\":false"));
        }
    }
    
    @Test
    void testHandleRequest_RetrieveAllLocations() throws Exception {
        // Arrange
        String userId = "user123";
        List<UserLocation> mockLocations = Arrays.asList(
            createMockUserLocation(userId, "2024-01-01T10:00:00Z", 40.7128, -74.0060),
            createMockUserLocation(userId, "2024-01-01T11:00:00Z", 40.7589, -73.9851)
        );
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        request.setQueryStringParameters(null);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", userId);
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    when(mock.getAllUserLocations(userId)).thenReturn(mockLocations);
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(200, response.getStatusCode());
            assertTrue(response.getBody().contains("\"success\":true"));
            assertTrue(response.getBody().contains("\"count\":2"));
            assertTrue(response.getBody().contains("\"locations\""));
            
            // Verify the service was called correctly
            DynamoDBService service = mockedConstruction.constructed().get(0);
            verify(service).getAllUserLocations(userId);
        }
    }
    
    @Test
    void testHandleRequest_RetrieveByTimeRange() throws Exception {
        // Arrange
        String userId = "user123";
        String startTime = "2024-01-01T10:00:00Z";
        String endTime = "2024-01-01T12:00:00Z";
        List<UserLocation> mockLocations = Arrays.asList(
            createMockUserLocation(userId, "2024-01-01T10:30:00Z", 40.7128, -74.0060),
            createMockUserLocation(userId, "2024-01-01T11:30:00Z", 40.7589, -73.9851)
        );
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("startTime", startTime);
        queryParams.put("endTime", endTime);
        request.setQueryStringParameters(queryParams);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", userId);
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    when(mock.getUserLocationsByTimeRange(userId, startTime, endTime)).thenReturn(mockLocations);
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(200, response.getStatusCode());
            assertTrue(response.getBody().contains("\"success\":true"));
            assertTrue(response.getBody().contains("\"count\":2"));
            
            // Verify the service was called correctly
            DynamoDBService service = mockedConstruction.constructed().get(0);
            verify(service).getUserLocationsByTimeRange(userId, startTime, endTime);
        }
    }
    
    @Test
    void testHandleRequest_RetrieveFromStartTime() throws Exception {
        // Arrange
        String userId = "user123";
        String startTime = "2024-01-01T10:00:00Z";
        List<UserLocation> mockLocations = Arrays.asList(
            createMockUserLocation(userId, "2024-01-01T10:30:00Z", 40.7128, -74.0060),
            createMockUserLocation(userId, "2024-01-01T11:30:00Z", 40.7589, -73.9851)
        );
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("startTime", startTime);
        request.setQueryStringParameters(queryParams);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", userId);
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    when(mock.getUserLocationsFromTime(userId, startTime)).thenReturn(mockLocations);
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(200, response.getStatusCode());
            assertTrue(response.getBody().contains("\"success\":true"));
            assertTrue(response.getBody().contains("\"count\":2"));
            
            // Verify the service was called correctly
            DynamoDBService service = mockedConstruction.constructed().get(0);
            verify(service).getUserLocationsFromTime(userId, startTime);
        }
    }
    
    @Test
    void testHandleRequest_RetrieveUpToEndTime() throws Exception {
        // Arrange
        String userId = "user123";
        String endTime = "2024-01-01T12:00:00Z";
        List<UserLocation> mockLocations = Arrays.asList(
            createMockUserLocation(userId, "2024-01-01T09:30:00Z", 40.7128, -74.0060),
            createMockUserLocation(userId, "2024-01-01T10:30:00Z", 40.7589, -73.9851)
        );
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("endTime", endTime);
        request.setQueryStringParameters(queryParams);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", userId);
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    when(mock.getUserLocationsUpToTime(userId, endTime)).thenReturn(mockLocations);
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(200, response.getStatusCode());
            assertTrue(response.getBody().contains("\"success\":true"));
            assertTrue(response.getBody().contains("\"count\":2"));
            
            // Verify the service was called correctly
            DynamoDBService service = mockedConstruction.constructed().get(0);
            verify(service).getUserLocationsUpToTime(userId, endTime);
        }
    }
    
    @Test
    void testHandleRequest_InvalidTimeRangeParameters() throws Exception {
        // Arrange
        String userId = "user123";
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("invalidParam", "value");
        request.setQueryStringParameters(queryParams);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", userId);
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    // Mock behavior if needed
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(400, response.getStatusCode());
            assertTrue(response.getBody().contains("Invalid time range parameters"));
            assertTrue(response.getBody().contains("\"success\":false"));
        }
    }
    
    @Test
    void testHandleRequest_EmptyResults() throws Exception {
        // Arrange
        String userId = "user123";
        List<UserLocation> mockLocations = new ArrayList<>();
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        request.setQueryStringParameters(null);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", userId);
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    when(mock.getAllUserLocations(userId)).thenReturn(mockLocations);
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(200, response.getStatusCode());
            assertTrue(response.getBody().contains("\"success\":true"));
            assertTrue(response.getBody().contains("\"count\":0"));
            assertTrue(response.getBody().contains("Successfully retrieved 0 user locations"));
        }
    }
    
    @Test
    void testHandleRequest_DynamoDBServiceException() throws Exception {
        // Arrange
        String userId = "user123";
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        request.setQueryStringParameters(null);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", userId);
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    when(mock.getAllUserLocations(userId)).thenThrow(new RuntimeException("Database error"));
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(500, response.getStatusCode());
            assertTrue(response.getBody().contains("Internal server error"));
            assertTrue(response.getBody().contains("\"success\":false"));
            
            // Verify error was logged
            verify(logger).log(contains("Error bulk retrieving user locations"));
        }
    }
    
    @Test
    void testHandleRequest_ResponseHeaders() throws Exception {
        // Arrange
        String userId = "user123";
        List<UserLocation> mockLocations = Arrays.asList(
            createMockUserLocation(userId, "2024-01-01T10:00:00Z", 40.7128, -74.0060)
        );
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        request.setPathParameters(pathParams);
        request.setQueryStringParameters(null);
        
        Map<String, Object> authorizerContext = new HashMap<>();
        authorizerContext.put("userId", userId);
        
        APIGatewayProxyRequestEvent.RequestContext requestContext = new APIGatewayProxyRequestEvent.RequestContext();
        requestContext.setAuthorizer(authorizerContext);
        request.setRequestContext(requestContext);
        
        try (MockedConstruction<DynamoDBService> mockedConstruction = 
                mockConstruction(DynamoDBService.class, (mock, context) -> {
                    when(mock.getAllUserLocations(userId)).thenReturn(mockLocations);
                })) {
            
            handler = new BulkRetrieveEntryHandler();
            
            // Act
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            
            // Assert
            assertEquals(200, response.getStatusCode());
            assertNotNull(response.getHeaders());
            assertEquals("application/json", response.getHeaders().get("Content-Type"));
        }
    }
    
    private UserLocation createMockUserLocation(String userId, String timestamp, double latitude, double longitude) {
        UserLocation location = new UserLocation();
        location.setUserId(userId);
        location.setTimestamp(timestamp);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAccuracy(10.0);
        location.setSpeed(5.0);
        location.setBearing(90.0);
        return location;
    }
} 