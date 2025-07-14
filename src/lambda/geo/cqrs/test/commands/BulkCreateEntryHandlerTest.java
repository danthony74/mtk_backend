package com.mindthekid.geo.cqrs.test.commands;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindthekid.cqrs.commands.BulkCreateEntryHandler;
import com.mindthekid.models.UserLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulkCreateEntryHandlerTest {

    @Mock
    private Context context;

    private BulkCreateEntryHandler handler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        handler = new BulkCreateEntryHandler();
    }

    @Test
    void testSuccessfulBulkCreateEntry() throws Exception {
        // Arrange
        List<UserLocation> userLocations = new ArrayList<>();
        
        UserLocation location1 = new UserLocation();
        location1.setUserId("user123");
        location1.setDateTime("2024-01-01T12:00:00Z");
        location1.setLatitude(40.7128);
        location1.setLongitude(-74.0060);
        
        UserLocation location2 = new UserLocation();
        location2.setUserId("user123");
        location2.setDateTime("2024-01-01T13:00:00Z");
        location2.setLatitude(40.7130);
        location2.setLongitude(-74.0062);
        
        userLocations.add(location1);
        userLocations.add(location2);

        String requestBody = objectMapper.writeValueAsString(userLocations);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(requestBody);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("success"));
        assertTrue(response.getBody().contains("Successfully created 2 user locations"));
    }

    @Test
    void testBulkCreateEntryWithEmptyArray() {
        // Arrange
        String requestBody = "[]";

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(requestBody);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("At least one user location is required"));
    }

    @Test
    void testBulkCreateEntryWithNullArray() {
        // Arrange
        String requestBody = "null";

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(requestBody);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("At least one user location is required"));
    }

    @Test
    void testBulkCreateEntryWithValidationErrors() throws Exception {
        // Arrange
        List<UserLocation> userLocations = new ArrayList<>();
        
        UserLocation validLocation = new UserLocation();
        validLocation.setUserId("user123");
        validLocation.setDateTime("2024-01-01T12:00:00Z");
        validLocation.setLatitude(40.7128);
        validLocation.setLongitude(-74.0060);
        
        UserLocation invalidLocation = new UserLocation();
        invalidLocation.setDateTime("2024-01-01T13:00:00Z");
        // Missing userId
        
        userLocations.add(validLocation);
        userLocations.add(invalidLocation);

        String requestBody = objectMapper.writeValueAsString(userLocations);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(requestBody);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Validation errors"));
        assertTrue(response.getBody().contains("user_id is required"));
    }

    @Test
    void testBulkCreateEntryWithMissingDateTime() throws Exception {
        // Arrange
        List<UserLocation> userLocations = new ArrayList<>();
        
        UserLocation location = new UserLocation();
        location.setUserId("user123");
        // Missing dateTime
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        
        userLocations.add(location);

        String requestBody = objectMapper.writeValueAsString(userLocations);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(requestBody);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("date_time is required"));
    }

    @Test
    void testBulkCreateEntryWithEmptyBody() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody("");
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Request body is required"));
    }

    @Test
    void testBulkCreateEntryWithNullBody() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(null);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Request body is required"));
    }

    @Test
    void testBulkCreateEntryWithInvalidJson() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody("[{ invalid json }]");
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Internal server error"));
    }

    @Test
    void testBulkCreateEntrySetsDefaultValues() throws Exception {
        // Arrange
        List<UserLocation> userLocations = new ArrayList<>();
        
        UserLocation location = new UserLocation();
        location.setUserId("user123");
        location.setDateTime("2024-01-01T12:00:00Z");
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        // isPrivate and isReal are null
        
        userLocations.add(location);

        String requestBody = objectMapper.writeValueAsString(userLocations);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(requestBody);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify default values were set
        String responseBody = response.getBody();
        assertTrue(responseBody.contains("\"isPrivate\":false"));
        assertTrue(responseBody.contains("\"isReal\":true"));
    }

    @Test
    void testBulkCreateEntryWithTimestamps() throws Exception {
        // Arrange
        List<UserLocation> userLocations = new ArrayList<>();
        
        UserLocation location = new UserLocation();
        location.setUserId("user123");
        location.setDateTime("2024-01-01T12:00:00Z");
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        
        userLocations.add(location);

        String requestBody = objectMapper.writeValueAsString(userLocations);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(requestBody);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify timestamps were set
        String responseBody = response.getBody();
        assertTrue(responseBody.contains("\"createdAt\":"));
        assertTrue(responseBody.contains("\"updatedAt\":"));
    }

    @Test
    void testBulkCreateEntryGeneratesLatLong() throws Exception {
        // Arrange
        List<UserLocation> userLocations = new ArrayList<>();
        
        UserLocation location = new UserLocation();
        location.setUserId("user123");
        location.setDateTime("2024-01-01T12:00:00Z");
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        
        userLocations.add(location);

        String requestBody = objectMapper.writeValueAsString(userLocations);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(requestBody);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify that lat_long was generated
        String responseBody = response.getBody();
        assertTrue(responseBody.contains("40.7128,-74.006"));
    }

    @Test
    void testBulkCreateEntryResponseStructure() throws Exception {
        // Arrange
        List<UserLocation> userLocations = new ArrayList<>();
        
        UserLocation location = new UserLocation();
        location.setUserId("user123");
        location.setDateTime("2024-01-01T12:00:00Z");
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        
        userLocations.add(location);

        String requestBody = objectMapper.writeValueAsString(userLocations);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(requestBody);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify response structure
        String responseBody = response.getBody();
        assertTrue(responseBody.contains("\"createdCount\":"));
        assertTrue(responseBody.contains("\"locations\":"));
        assertTrue(responseBody.contains("\"success\":true"));
    }

    @Test
    void testBulkCreateEntryResponseHeaders() throws Exception {
        // Arrange
        List<UserLocation> userLocations = new ArrayList<>();
        
        UserLocation location = new UserLocation();
        location.setUserId("user123");
        location.setDateTime("2024-01-01T12:00:00Z");
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        
        userLocations.add(location);

        String requestBody = objectMapper.writeValueAsString(userLocations);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody(requestBody);
        request.setHeaders(new HashMap<>());

        when(context.getLogger()).thenReturn(System.out::println);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getHeaders());
        assertEquals("application/json", response.getHeaders().get("Content-Type"));
    }
} 