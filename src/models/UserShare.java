package com.mindthekid.models;

import java.time.LocalDateTime;

/**
 * UserShare entity representing sharing relationships between users in the MTK Backend system.
 * 
 * This entity represents sharing relationships where one user shares something
 * with another user, including the type of sharing and when it occurred.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class UserShare {
    private User sharedBy;
    private User sharedWith;
    private ShareType shareType;
    private LocalDateTime whenShared;

    /**
     * Default constructor
     */
    public UserShare() {}

    /**
     * Constructor with required fields
     * 
     * @param sharedBy User who is sharing
     * @param sharedWith User who is being shared with
     * @param shareType Type of sharing relationship
     * @param whenShared When the sharing occurred
     */
    public UserShare(User sharedBy, User sharedWith, ShareType shareType, LocalDateTime whenShared) {
        this.sharedBy = sharedBy;
        this.sharedWith = sharedWith;
        this.shareType = shareType;
        this.whenShared = whenShared;
    }

    // Getters and Setters
    public User getSharedBy() { return sharedBy; }
    public void setSharedBy(User sharedBy) { this.sharedBy = sharedBy; }

    public User getSharedWith() { return sharedWith; }
    public void setSharedWith(User sharedWith) { this.sharedWith = sharedWith; }

    public ShareType getShareType() { return shareType; }
    public void setShareType(ShareType shareType) { this.shareType = shareType; }

    public LocalDateTime getWhenShared() { return whenShared; }
    public void setWhenShared(LocalDateTime whenShared) { this.whenShared = whenShared; }

    /**
     * Get the ID of the user who is sharing
     * 
     * @return Shared by user ID, null if user is not set
     */
    public String getSharedById() {
        return sharedBy != null ? sharedBy.getId() : null;
    }

    /**
     * Get the name of the user who is sharing
     * 
     * @return Shared by user name, null if user is not set
     */
    public String getSharedByName() {
        return sharedBy != null ? sharedBy.getDisplayName() : null;
    }

    /**
     * Get the ID of the user who is being shared with
     * 
     * @return Shared with user ID, null if user is not set
     */
    public String getSharedWithId() {
        return sharedWith != null ? sharedWith.getId() : null;
    }

    /**
     * Get the name of the user who is being shared with
     * 
     * @return Shared with user name, null if user is not set
     */
    public String getSharedWithName() {
        return sharedWith != null ? sharedWith.getDisplayName() : null;
    }

    /**
     * Get the share type ID
     * 
     * @return Share type ID, null if share type is not set
     */
    public Integer getShareTypeId() {
        return shareType != null ? shareType.getId() : null;
    }

    /**
     * Get the share type code
     * 
     * @return Share type code, null if share type is not set
     */
    public String getShareTypeCode() {
        return shareType != null ? shareType.getShareTypeCode() : null;
    }

    /**
     * Get the share type description
     * 
     * @return Share type description, null if share type is not set
     */
    public String getShareTypeDesc() {
        return shareType != null ? shareType.getShareTypeDesc() : null;
    }

    /**
     * Get the share date (date part only)
     * 
     * @return Share date, null if whenShared is not set
     */
    public java.time.LocalDate getShareDate() {
        return whenShared != null ? whenShared.toLocalDate() : null;
    }

    /**
     * Get the share time (time part only)
     * 
     * @return Share time, null if whenShared is not set
     */
    public java.time.LocalTime getShareTime() {
        return whenShared != null ? whenShared.toLocalTime() : null;
    }

    /**
     * Check if this is a family sharing relationship
     * 
     * @return true if this is a family sharing relationship, false otherwise
     */
    public boolean isFamilySharing() {
        return shareType != null && shareType.isFamilySharing();
    }

    /**
     * Check if this is a friend sharing relationship
     * 
     * @return true if this is a friend sharing relationship, false otherwise
     */
    public boolean isFriendSharing() {
        return shareType != null && shareType.isFriendSharing();
    }

    /**
     * Check if this is a business sharing relationship
     * 
     * @return true if this is a business sharing relationship, false otherwise
     */
    public boolean isBusinessSharing() {
        return shareType != null && shareType.isBusinessSharing();
    }

    /**
     * Check if this is a public sharing relationship
     * 
     * @return true if this is a public sharing relationship, false otherwise
     */
    public boolean isPublicSharing() {
        return shareType != null && shareType.isPublicSharing();
    }

    /**
     * Check if this is a private sharing relationship
     * 
     * @return true if this is a private sharing relationship, false otherwise
     */
    public boolean isPrivateSharing() {
        return shareType != null && shareType.isPrivateSharing();
    }

    /**
     * Check if this is a temporary sharing relationship
     * 
     * @return true if this is a temporary sharing relationship, false otherwise
     */
    public boolean isTemporarySharing() {
        return shareType != null && shareType.isTemporarySharing();
    }

    /**
     * Check if this is a permanent sharing relationship
     * 
     * @return true if this is a permanent sharing relationship, false otherwise
     */
    public boolean isPermanentSharing() {
        return shareType != null && shareType.isPermanentSharing();
    }

    /**
     * Check if this sharing relationship requires approval
     * 
     * @return true if this sharing relationship requires approval, false otherwise
     */
    public boolean requiresApproval() {
        return shareType != null && shareType.requiresApproval();
    }

    /**
     * Check if this sharing relationship is read-only
     * 
     * @return true if this sharing relationship is read-only, false otherwise
     */
    public boolean isReadOnly() {
        return shareType != null && shareType.isReadOnly();
    }

    /**
     * Check if this sharing relationship allows full access
     * 
     * @return true if this sharing relationship allows full access, false otherwise
     */
    public boolean allowsFullAccess() {
        return shareType != null && shareType.allowsFullAccess();
    }

    /**
     * Get the duration since sharing in days
     * 
     * @return Number of days since sharing, or null if whenShared is not set
     */
    public Long getDaysSinceSharing() {
        if (whenShared == null) return null;
        
        return java.time.temporal.ChronoUnit.DAYS.between(whenShared, LocalDateTime.now());
    }

    /**
     * Get the duration since sharing in hours
     * 
     * @return Number of hours since sharing, or null if whenShared is not set
     */
    public Long getHoursSinceSharing() {
        if (whenShared == null) return null;
        
        return java.time.temporal.ChronoUnit.HOURS.between(whenShared, LocalDateTime.now());
    }

    /**
     * Check if this sharing is recent (within the last 24 hours)
     * 
     * @return true if sharing is recent, false otherwise
     */
    public boolean isRecent() {
        Long hoursSince = getHoursSinceSharing();
        return hoursSince != null && hoursSince < 24;
    }

    /**
     * Check if this sharing is today
     * 
     * @return true if sharing is today, false otherwise
     */
    public boolean isToday() {
        if (whenShared == null) return false;
        
        LocalDateTime now = LocalDateTime.now();
        return whenShared.toLocalDate().equals(now.toLocalDate());
    }

    /**
     * Get a formatted description of the sharing relationship
     * 
     * @return Formatted description string
     */
    public String getSharingDescription() {
        StringBuilder description = new StringBuilder();
        
        if (sharedBy != null) {
            description.append(sharedBy.getDisplayName());
        } else {
            description.append("Unknown User");
        }
        
        description.append(" shared with ");
        
        if (sharedWith != null) {
            description.append(sharedWith.getDisplayName());
        } else {
            description.append("Unknown User");
        }
        
        if (shareType != null) {
            description.append(" (");
            description.append(shareType.getShareTypeDesc());
            description.append(")");
        }
        
        return description.toString();
    }

    @Override
    public String toString() {
        return String.format("UserShare{sharedBy='%s', sharedWith='%s', shareType='%s', whenShared=%s}", 
            sharedBy != null ? sharedBy.getId() : "null",
            sharedWith != null ? sharedWith.getId() : "null",
            shareType != null ? shareType.getShareTypeCode() : "null",
            whenShared);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UserShare that = (UserShare) obj;
        
        if (sharedBy != null ? !sharedBy.equals(that.sharedBy) : that.sharedBy != null) return false;
        if (sharedWith != null ? !sharedWith.equals(that.sharedWith) : that.sharedWith != null) return false;
        return whenShared != null ? whenShared.equals(that.whenShared) : that.whenShared == null;
    }

    @Override
    public int hashCode() {
        int result = sharedBy != null ? sharedBy.hashCode() : 0;
        result = 31 * result + (sharedWith != null ? sharedWith.hashCode() : 0);
        result = 31 * result + (whenShared != null ? whenShared.hashCode() : 0);
        return result;
    }

    /**
     * Convert this UserShare object to JSON string
     * 
     * @return JSON representation of the UserShare
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting UserShare to JSON", e);
        }
    }

    /**
     * Create a UserShare object from JSON string
     * 
     * @param json JSON string representation of UserShare
     * @return UserShare object
     */
    public static UserShare fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.readValue(json, UserShare.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to UserShare", e);
        }
    }

    /**
     * Convert this UserShare object to XML string
     * 
     * @return XML representation of the UserShare
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(UserShare.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting UserShare to XML", e);
        }
    }

    /**
     * Create a UserShare object from XML string
     * 
     * @param xml XML string representation of UserShare
     * @return UserShare object
     */
    public static UserShare fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(UserShare.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (UserShare) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to UserShare", e);
        }
    }
} 