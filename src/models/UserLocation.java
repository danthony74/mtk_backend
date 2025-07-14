package com.mindthekid.models;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import java.time.Instant;

@DynamoDBTable(tableName = "user_locations")
public class UserLocation {
    
    private String userId;
    private String dateTime;
    private Double latitude;
    private Double longitude;
    private Boolean isPrivate;
    private Boolean isReal;
    private String latLong;
    private Instant createdAt;
    private Instant updatedAt;
    
    @DynamoDBHashKey(attributeName = "user_id")
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @DynamoDBRangeKey(attributeName = "date_time")
    public String getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    
    @DynamoDBAttribute(attributeName = "latitude")
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    @DynamoDBAttribute(attributeName = "longitude")
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    @DynamoDBAttribute(attributeName = "is_private")
    public Boolean getIsPrivate() {
        return isPrivate;
    }
    
    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    
    @DynamoDBAttribute(attributeName = "is_real")
    public Boolean getIsReal() {
        return isReal;
    }
    
    public void setIsReal(Boolean isReal) {
        this.isReal = isReal;
    }
    
    @DynamoDBAttribute(attributeName = "lat_long")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "lat_long_index")
    public String getLatLong() {
        return latLong;
    }
    
    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }
    
    @DynamoDBAttribute(attributeName = "created_at")
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    @DynamoDBAttribute(attributeName = "updated_at")
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "UserLocation{" +
                "userId='" + userId + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isPrivate=" + isPrivate +
                ", isReal=" + isReal +
                ", latLong='" + latLong + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 