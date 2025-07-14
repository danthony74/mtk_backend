package com.mindthekid.geo.cqrs.test.commands;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindthekid.cqrs.commands.CreateEntryHandler;
import com.mindthekid.cqrs.shared.models.UserLocation;
import com.mindthekid.cqrs.shared.services.DynamoDBService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateEntryHandlerTest {

    @Mock
    private Context context;

    @Mock
    private DynamoDBService dynamoDBService;

    private CreateEntryHandler handler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        handler = new CreateEntryHandler();
    }

    @Test
    void testSuccessfulCreateEntry() throws Exception {
        // Arrange
        UserLocation userLocation = new UserLocation();
        userLocation.setUserId("user123");
        userLocation.setDateTime("2024-01-01T12:00:00Z");
        userLocation.setLatitude(40.7128);
        userLocation.setLongitude(-74.0060);
        userLocation.setIsPrivate(false);
        userLocation.setIsReal(true);

        String requestBody = objectMapper.writeValueAsString(userLocation);

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
        assertTrue(response.getBody().contains("User location created successfully"));
    }

    @Test
    void testCreateEntryWithMissingUserId() throws Exception {
        // Arrange
        UserLocation userLocation = new UserLocation();
        userLocation.setDateTime("2024-01-01T12:00:00Z");
        userLocation.setLatitude(40.7128);
        userLocation.setLongitude(-74.0060);

        String requestBody = objectMapper.writeValueAsString(userLocation);

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
        assertTrue(response.getBody().contains("user_id is required"));
    }

    @Test
    void testCreateEntryWithMissingDateTime() throws Exception {
        // Arrange
        UserLocation userLocation = new UserLocation();
        userLocation.setUserId("user123");
        userLocation.setLatitude(40.7128);
        userLocation.setLongitude(-74.0060);

        String requestBody = objectMapper.writeValueAsString(userLocation);

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
    void testCreateEntryWithEmptyBody() {
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
    void testCreateEntryWithNullBody() {
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
    void testCreateEntryWithInvalidJson() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody("{ invalid json }");
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
    void testCreateEntryWithCoordinatesGeneratesLatLong() throws Exception {
        // Arrange
        UserLocation userLocation = new UserLocation();
        userLocation.setUserId("user123");
        userLocation.setDateTime("2024-01-01T12:00:00Z");
        userLocation.setLatitude(40.7128);
        userLocation.setLongitude(-74.0060);

        String requestBody = objectMapper.writeValueAsString(userLocation);

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
    void testCreateEntrySetsDefaultValues() throws Exception {
        // Arrange
        UserLocation userLocation = new UserLocation();
        userLocation.setUserId("user123");
        userLocation.setDateTime("2024-01-01T12:00:00Z");
        userLocation.setLatitude(40.7128);
        userLocation.setLongitude(-74.0060);
        // isPrivate and isReal are null

        String requestBody = objectMapper.writeValueAsString(userLocation);

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
    void testCreateEntryWithTimestamps() throws Exception {
        // Arrange
        UserLocation userLocation = new UserLocation();
        userLocation.setUserId("user123");
        userLocation.setDateTime("2024-01-01T12:00:00Z");
        userLocation.setLatitude(40.7128);
        userLocation.setLongitude(-74.0060);

        String requestBody = objectMapper.writeValueAsString(userLocation);

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
    void testCreateEntryResponseHeaders() throws Exception {
        // Arrange
        UserLocation userLocation = new UserLocation();
        userLocation.setUserId("user123");
        userLocation.setDateTime("2024-01-01T12:00:00Z");
        userLocation.setLatitude(40.7128);
        userLocation.setLongitude(-74.0060);

        String requestBody = objectMapper.writeValueAsString(userLocation);

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