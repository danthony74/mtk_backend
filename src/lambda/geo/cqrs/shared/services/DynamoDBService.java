package com.mindthekid.geo.cqrs.shared.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.mindthekid.models.UserLocation;

import java.util.*;
import java.util.stream.Collectors;

public class DynamoDBService {
    
    private final DynamoDBMapper dynamoDBMapper;
    private final AmazonDynamoDB dynamoDBClient;
    private final String tableName;
    
    public DynamoDBService() {
        this.dynamoDBClient = AmazonDynamoDBClientBuilder.standard().build();
        this.dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
        
        // Get table name from environment variable, fallback to default
        this.tableName = System.getenv("DYNAMODB_TABLE_NAME");
        if (this.tableName == null || this.tableName.trim().isEmpty()) {
            throw new IllegalStateException("DYNAMODB_TABLE_NAME environment variable is required");
        }
    }
    
    // Single operations
    public void saveUserLocation(UserLocation userLocation) {
        dynamoDBMapper.save(userLocation);
    }
    
    public UserLocation getUserLocation(String userId, String dateTime) {
        UserLocation key = new UserLocation();
        key.setUserId(userId);
        key.setDateTime(dateTime);
        return dynamoDBMapper.load(key);
    }
    
    public void deleteUserLocation(String userId, String dateTime) {
        UserLocation key = new UserLocation();
        key.setUserId(userId);
        key.setDateTime(dateTime);
        dynamoDBMapper.delete(key);
    }
    
    // Bulk operations
    public List<UserLocation> bulkSaveUserLocations(List<UserLocation> userLocations) {
        // Process in batches of 25 (DynamoDB batch limit)
        List<UserLocation> savedLocations = new ArrayList<>();
        
        for (int i = 0; i < userLocations.size(); i += 25) {
            int endIndex = Math.min(i + 25, userLocations.size());
            List<UserLocation> batch = userLocations.subList(i, endIndex);
            
            // Save batch
            dynamoDBMapper.batchSave(batch);
            savedLocations.addAll(batch);
        }
        
        return savedLocations;
    }
    
    public List<UserLocation> getAllUserLocations(String userId) {
        UserLocation key = new UserLocation();
        key.setUserId(userId);
        
        DynamoDBQueryExpression<UserLocation> queryExpression = new DynamoDBQueryExpression<UserLocation>()
                .withHashKeyValues(key);
        
        return dynamoDBMapper.query(UserLocation.class, queryExpression);
    }
    
    public List<UserLocation> getUserLocationsByTimeRange(String userId, String startTime, String endTime) {
        UserLocation key = new UserLocation();
        key.setUserId(userId);
        
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":startTime", new AttributeValue().withS(startTime));
        expressionAttributeValues.put(":endTime", new AttributeValue().withS(endTime));
        
        DynamoDBQueryExpression<UserLocation> queryExpression = new DynamoDBQueryExpression<UserLocation>()
                .withHashKeyValues(key)
                .withRangeKeyCondition("date_time BETWEEN :startTime AND :endTime")
                .withExpressionAttributeValues(expressionAttributeValues);
        
        return dynamoDBMapper.query(UserLocation.class, queryExpression);
    }
    
    public List<UserLocation> getUserLocationsFromTime(String userId, String startTime) {
        UserLocation key = new UserLocation();
        key.setUserId(userId);
        
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":startTime", new AttributeValue().withS(startTime));
        
        DynamoDBQueryExpression<UserLocation> queryExpression = new DynamoDBQueryExpression<UserLocation>()
                .withHashKeyValues(key)
                .withRangeKeyCondition("date_time >= :startTime")
                .withExpressionAttributeValues(expressionAttributeValues);
        
        return dynamoDBMapper.query(UserLocation.class, queryExpression);
    }
    
    public List<UserLocation> getUserLocationsUpToTime(String userId, String endTime) {
        UserLocation key = new UserLocation();
        key.setUserId(userId);
        
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":endTime", new AttributeValue().withS(endTime));
        
        DynamoDBQueryExpression<UserLocation> queryExpression = new DynamoDBQueryExpression<UserLocation>()
                .withHashKeyValues(key)
                .withRangeKeyCondition("date_time <= :endTime")
                .withExpressionAttributeValues(expressionAttributeValues);
        
        return dynamoDBMapper.query(UserLocation.class, queryExpression);
    }
    
    public List<UserLocation> deleteAllUserLocations(String userId) {
        // First retrieve all locations
        List<UserLocation> locations = getAllUserLocations(userId);
        
        if (locations.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Delete in batches
        for (int i = 0; i < locations.size(); i += 25) {
            int endIndex = Math.min(i + 25, locations.size());
            List<UserLocation> batch = locations.subList(i, endIndex);
            dynamoDBMapper.batchDelete(batch);
        }
        
        return locations;
    }
    
    public List<UserLocation> deleteUserLocationsByTimeRange(String userId, String startTime, String endTime) {
        // First retrieve locations in time range
        List<UserLocation> locations = getUserLocationsByTimeRange(userId, startTime, endTime);
        
        if (locations.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Delete in batches
        for (int i = 0; i < locations.size(); i += 25) {
            int endIndex = Math.min(i + 25, locations.size());
            List<UserLocation> batch = locations.subList(i, endIndex);
            dynamoDBMapper.batchDelete(batch);
        }
        
        return locations;
    }
    
    public List<UserLocation> deleteUserLocationsFromTime(String userId, String startTime) {
        // First retrieve locations from start time
        List<UserLocation> locations = getUserLocationsFromTime(userId, startTime);
        
        if (locations.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Delete in batches
        for (int i = 0; i < locations.size(); i += 25) {
            int endIndex = Math.min(i + 25, locations.size());
            List<UserLocation> batch = locations.subList(i, endIndex);
            dynamoDBMapper.batchDelete(batch);
        }
        
        return locations;
    }
    
    public List<UserLocation> deleteUserLocationsUpToTime(String userId, String endTime) {
        // First retrieve locations up to end time
        List<UserLocation> locations = getUserLocationsUpToTime(userId, endTime);
        
        if (locations.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Delete in batches
        for (int i = 0; i < locations.size(); i += 25) {
            int endIndex = Math.min(i + 25, locations.size());
            List<UserLocation> batch = locations.subList(i, endIndex);
            dynamoDBMapper.batchDelete(batch);
        }
        
        return locations;
    }
    
    // Utility methods
    public String getTableName() {
        return this.tableName;
    }
    
    public void close() {
        if (dynamoDBClient != null) {
            dynamoDBClient.shutdown();
        }
    }
} 