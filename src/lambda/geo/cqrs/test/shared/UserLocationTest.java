package com.mindthekid.geo.cqrs.test.shared;

import com.mindthekid.cqrs.shared.models.UserLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserLocationTest {

    private UserLocation userLocation;

    @BeforeEach
    void setUp() {
        userLocation = new UserLocation();
    }

    @Test
    void testUserLocationSettersAndGetters() {
        // Arrange
        String userId = "user123";
        String dateTime = "2024-01-01T12:00:00Z";
        Double latitude = 40.7128;
        Double longitude = -74.0060;
        Boolean isPrivate = false;
        Boolean isReal = true;
        String latLong = "40.7128,-74.0060";
        Instant now = Instant.now();

        // Act
        userLocation.setUserId(userId);
        userLocation.setDateTime(dateTime);
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);
        userLocation.setIsPrivate(isPrivate);
        userLocation.setIsReal(isReal);
        userLocation.setLatLong(latLong);
        userLocation.setCreatedAt(now);
        userLocation.setUpdatedAt(now);

        // Assert
        assertEquals(userId, userLocation.getUserId());
        assertEquals(dateTime, userLocation.getDateTime());
        assertEquals(latitude, userLocation.getLatitude());
        assertEquals(longitude, userLocation.getLongitude());
        assertEquals(isPrivate, userLocation.getIsPrivate());
        assertEquals(isReal, userLocation.getIsReal());
        assertEquals(latLong, userLocation.getLatLong());
        assertEquals(now, userLocation.getCreatedAt());
        assertEquals(now, userLocation.getUpdatedAt());
    }

    @Test
    void testUserLocationWithNullValues() {
        // Act
        userLocation.setUserId(null);
        userLocation.setDateTime(null);
        userLocation.setLatitude(null);
        userLocation.setLongitude(null);
        userLocation.setIsPrivate(null);
        userLocation.setIsReal(null);
        userLocation.setLatLong(null);
        userLocation.setCreatedAt(null);
        userLocation.setUpdatedAt(null);

        // Assert
        assertNull(userLocation.getUserId());
        assertNull(userLocation.getDateTime());
        assertNull(userLocation.getLatitude());
        assertNull(userLocation.getLongitude());
        assertNull(userLocation.getIsPrivate());
        assertNull(userLocation.getIsReal());
        assertNull(userLocation.getLatLong());
        assertNull(userLocation.getCreatedAt());
        assertNull(userLocation.getUpdatedAt());
    }

    @Test
    void testUserLocationToString() {
        // Arrange
        userLocation.setUserId("user123");
        userLocation.setDateTime("2024-01-01T12:00:00Z");
        userLocation.setLatitude(40.7128);
        userLocation.setLongitude(-74.0060);
        userLocation.setIsPrivate(false);
        userLocation.setIsReal(true);
        userLocation.setLatLong("40.7128,-74.0060");

        // Act
        String result = userLocation.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("UserLocation{"));
        assertTrue(result.contains("userId='user123'"));
        assertTrue(result.contains("dateTime='2024-01-01T12:00:00Z'"));
        assertTrue(result.contains("latitude=40.7128"));
        assertTrue(result.contains("longitude=-74.006"));
        assertTrue(result.contains("isPrivate=false"));
        assertTrue(result.contains("isReal=true"));
        assertTrue(result.contains("latLong='40.7128,-74.0060'"));
    }

    @Test
    void testUserLocationToStringWithNullValues() {
        // Act
        String result = userLocation.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("UserLocation{"));
        assertTrue(result.contains("userId='null'"));
        assertTrue(result.contains("dateTime='null'"));
        assertTrue(result.contains("latitude=null"));
        assertTrue(result.contains("longitude=null"));
        assertTrue(result.contains("isPrivate=null"));
        assertTrue(result.contains("isReal=null"));
        assertTrue(result.contains("latLong='null'"));
    }

    @Test
    void testUserLocationEquality() {
        // Arrange
        UserLocation location1 = new UserLocation();
        location1.setUserId("user123");
        location1.setDateTime("2024-01-01T12:00:00Z");
        location1.setLatitude(40.7128);
        location1.setLongitude(-74.0060);

        UserLocation location2 = new UserLocation();
        location2.setUserId("user123");
        location2.setDateTime("2024-01-01T12:00:00Z");
        location2.setLatitude(40.7128);
        location2.setLongitude(-74.0060);

        // Act & Assert
        assertEquals(location1.getUserId(), location2.getUserId());
        assertEquals(location1.getDateTime(), location2.getDateTime());
        assertEquals(location1.getLatitude(), location2.getLatitude());
        assertEquals(location1.getLongitude(), location2.getLongitude());
    }

    @Test
    void testUserLocationWithSpecialCharacters() {
        // Arrange
        String userId = "user-123_test@example.com";
        String dateTime = "2024-01-01T12:00:00.000Z";
        String latLong = "40.7128,-74.0060,100.5";

        // Act
        userLocation.setUserId(userId);
        userLocation.setDateTime(dateTime);
        userLocation.setLatLong(latLong);

        // Assert
        assertEquals(userId, userLocation.getUserId());
        assertEquals(dateTime, userLocation.getDateTime());
        assertEquals(latLong, userLocation.getLatLong());
    }

    @Test
    void testUserLocationWithExtremeValues() {
        // Arrange
        Double maxLatitude = 90.0;
        Double minLatitude = -90.0;
        Double maxLongitude = 180.0;
        Double minLongitude = -180.0;

        // Act
        userLocation.setLatitude(maxLatitude);
        userLocation.setLongitude(maxLongitude);

        // Assert
        assertEquals(maxLatitude, userLocation.getLatitude());
        assertEquals(maxLongitude, userLocation.getLongitude());

        // Act
        userLocation.setLatitude(minLatitude);
        userLocation.setLongitude(minLongitude);

        // Assert
        assertEquals(minLatitude, userLocation.getLatitude());
        assertEquals(minLongitude, userLocation.getLongitude());
    }

    @Test
    void testUserLocationWithLongStrings() {
        // Arrange
        String longUserId = "very_long_user_id_that_exceeds_normal_length_but_should_still_be_valid_for_testing_purposes";
        String longDateTime = "2024-12-31T23:59:59.999999999Z";

        // Act
        userLocation.setUserId(longUserId);
        userLocation.setDateTime(longDateTime);

        // Assert
        assertEquals(longUserId, userLocation.getUserId());
        assertEquals(longDateTime, userLocation.getDateTime());
    }

    @Test
    void testUserLocationWithDecimalPrecision() {
        // Arrange
        Double preciseLatitude = 40.712823456789;
        Double preciseLongitude = -74.006012345678;

        // Act
        userLocation.setLatitude(preciseLatitude);
        userLocation.setLongitude(preciseLongitude);

        // Assert
        assertEquals(preciseLatitude, userLocation.getLatitude());
        assertEquals(preciseLongitude, userLocation.getLongitude());
    }

    @Test
    void testUserLocationWithBooleanValues() {
        // Test all boolean combinations
        boolean[][] combinations = {
            {true, true},
            {true, false},
            {false, true},
            {false, false}
        };

        for (boolean[] combo : combinations) {
            // Arrange
            boolean isPrivate = combo[0];
            boolean isReal = combo[1];

            // Act
            userLocation.setIsPrivate(isPrivate);
            userLocation.setIsReal(isReal);

            // Assert
            assertEquals(isPrivate, userLocation.getIsPrivate());
            assertEquals(isReal, userLocation.getIsReal());
        }
    }

    @Test
    void testUserLocationWithInstantValues() {
        // Arrange
        Instant past = Instant.parse("2020-01-01T00:00:00Z");
        Instant present = Instant.now();
        Instant future = Instant.parse("2030-01-01T00:00:00Z");

        // Act & Assert
        userLocation.setCreatedAt(past);
        assertEquals(past, userLocation.getCreatedAt());

        userLocation.setUpdatedAt(present);
        assertEquals(present, userLocation.getUpdatedAt());

        userLocation.setCreatedAt(future);
        assertEquals(future, userLocation.getCreatedAt());
    }
} 