package com.mindthekid.geo.cqrs.shared.models;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * FamilyMembership entity representing the many-to-many relationship between users and families.
 * 
 * This junction entity connects users with their families and includes additional
 * metadata such as membership type, join date, and optional leave date for tracking
 * when users join or leave families.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class FamilyMembership {
    private Integer id;
    private Family family;
    private User user;
    private FamilyMemberType membershipType;
    private LocalDateTime whenJoined;
    private Optional<LocalDateTime> whenLeft;

    /**
     * Default constructor
     */
    public FamilyMembership() {}

    /**
     * Constructor with required fields
     * 
     * @param family Associated family
     * @param user Associated user
     * @param membershipType Type of membership (parent, child, etc.)
     * @param whenJoined When user joined the family
     */
    public FamilyMembership(Family family, User user, FamilyMemberType membershipType, LocalDateTime whenJoined) {
        this.family = family;
        this.user = user;
        this.membershipType = membershipType;
        this.whenJoined = whenJoined;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param family Associated family
     * @param user Associated user
     * @param membershipType Type of membership (parent, child, etc.)
     * @param whenJoined When user joined the family
     * @param whenLeft When user left the family (optional)
     */
    public FamilyMembership(Integer id, Family family, User user, FamilyMemberType membershipType, 
                           LocalDateTime whenJoined, Optional<LocalDateTime> whenLeft) {
        this.id = id;
        this.family = family;
        this.user = user;
        this.membershipType = membershipType;
        this.whenJoined = whenJoined;
        this.whenLeft = whenLeft;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Family getFamily() { return family; }
    public void setFamily(Family family) { this.family = family; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public FamilyMemberType getMembershipType() { return membershipType; }
    public void setMembershipType(FamilyMemberType membershipType) { this.membershipType = membershipType; }

    public LocalDateTime getWhenJoined() { return whenJoined; }
    public void setWhenJoined(LocalDateTime whenJoined) { this.whenJoined = whenJoined; }

    public Optional<LocalDateTime> getWhenLeft() { return whenLeft; }
    public void setWhenLeft(Optional<LocalDateTime> whenLeft) { this.whenLeft = whenLeft; }

    /**
     * Check if this family membership is currently active
     * 
     * @return true if the membership is active, false otherwise
     */
    public boolean isActive() {
        return whenLeft == null || !whenLeft.isPresent();
    }

    /**
     * Check if this family membership is active at a specific time
     * 
     * @param dateTime The date/time to check
     * @return true if the membership is active at the specified time, false otherwise
     */
    public boolean isActiveAt(LocalDateTime dateTime) {
        if (dateTime == null) return false;
        
        // Check if date/time is after or equal to join time
        if (dateTime.isBefore(whenJoined)) {
            return false;
        }
        
        // Check if date/time is before leave time (if leave time exists)
        if (whenLeft != null && whenLeft.isPresent() && !dateTime.isBefore(whenLeft.get())) {
            return false;
        }
        
        return true;
    }

    /**
     * Get the duration of this membership in days
     * 
     * @return Number of days, or null if still active
     */
    public Long getDurationInDays() {
        if (whenLeft == null || !whenLeft.isPresent()) {
            return null; // Still active
        }
        
        return java.time.temporal.ChronoUnit.DAYS.between(whenJoined, whenLeft.get());
    }

    /**
     * Get the family ID
     * 
     * @return Family ID, null if family is not set
     */
    public Integer getFamilyId() {
        return family != null ? family.getId() : null;
    }

    /**
     * Get the family name
     * 
     * @return Family name, null if family is not set
     */
    public String getFamilyName() {
        return family != null ? family.getFamilyName() : null;
    }

    /**
     * Get the user ID
     * 
     * @return User ID, null if user is not set
     */
    public String getUserId() {
        return user != null ? user.getId() : null;
    }

    /**
     * Get the user's display name
     * 
     * @return User's display name, null if user is not set
     */
    public String getUserName() {
        return user != null ? user.getDisplayName() : null;
    }

    /**
     * Get the membership type code
     * 
     * @return Membership type code, null if membership type is not set
     */
    public String getMembershipTypeCode() {
        return membershipType != null ? membershipType.getMemberTypeCode() : null;
    }

    /**
     * Get the membership type description
     * 
     * @return Membership type description, null if membership type is not set
     */
    public String getMembershipTypeDesc() {
        return membershipType != null ? membershipType.getMemberTypeDesc() : null;
    }

    /**
     * Check if this is a parent membership
     * 
     * @return true if this is a parent membership, false otherwise
     */
    public boolean isParent() {
        return membershipType != null && membershipType.isParent();
    }

    /**
     * Check if this is a child membership
     * 
     * @return true if this is a child membership, false otherwise
     */
    public boolean isChild() {
        return membershipType != null && membershipType.isChild();
    }

    /**
     * Check if this is a guardian membership
     * 
     * @return true if this is a guardian membership, false otherwise
     */
    public boolean isGuardian() {
        return membershipType != null && membershipType.isGuardian();
    }

    /**
     * Check if this is a sibling membership
     * 
     * @return true if this is a sibling membership, false otherwise
     */
    public boolean isSibling() {
        return membershipType != null && membershipType.isSibling();
    }

    /**
     * Check if this is an adult membership
     * 
     * @return true if this is an adult membership, false otherwise
     */
    public boolean isAdult() {
        return membershipType != null && membershipType.isAdult();
    }

    /**
     * Check if this is a minor membership
     * 
     * @return true if this is a minor membership, false otherwise
     */
    public boolean isMinor() {
        return membershipType != null && membershipType.isMinor();
    }

    /**
     * Leave the family
     * 
     * @param leaveTime The date/time when the user left the family
     */
    public void leaveFamily(LocalDateTime leaveTime) {
        this.whenLeft = Optional.of(leaveTime);
    }

    /**
     * Rejoin the family
     */
    public void rejoinFamily() {
        this.whenLeft = Optional.empty();
    }

    /**
     * Get the join date (date part only)
     * 
     * @return Join date, null if whenJoined is not set
     */
    public java.time.LocalDate getJoinDate() {
        return whenJoined != null ? whenJoined.toLocalDate() : null;
    }

    /**
     * Get the leave date (date part only)
     * 
     * @return Leave date, null if whenLeft is not set
     */
    public java.time.LocalDate getLeaveDate() {
        return whenLeft != null && whenLeft.isPresent() ? whenLeft.get().toLocalDate() : null;
    }

    @Override
    public String toString() {
        return String.format("FamilyMembership{id=%d, family='%s', user='%s', membershipType='%s', active=%s}", 
            id, 
            family != null ? family.getFamilyName() : "null",
            user != null ? user.getId() : "null",
            membershipType != null ? membershipType.getMemberTypeCode() : "null",
            isActive());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        FamilyMembership that = (FamilyMembership) obj;
        
        if (family != null ? !family.equals(that.family) : that.family != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return whenJoined != null ? whenJoined.equals(that.whenJoined) : that.whenJoined == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (family != null ? family.hashCode() : 0);
        result = prime * result + (user != null ? user.hashCode() : 0);
        result = prime * result + (membershipType != null ? membershipType.hashCode() : 0);
        result = prime * result + (whenJoined != null ? whenJoined.hashCode() : 0);
        result = prime * result + (whenLeft != null ? whenLeft.hashCode() : 0);
        return result;
    }

    /**
     * Convert this FamilyMembership object to JSON string
     * 
     * @return JSON representation of the FamilyMembership
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting FamilyMembership to JSON", e);
        }
    }

    /**
     * Create a FamilyMembership object from JSON string
     * 
     * @param json JSON string representation of FamilyMembership
     * @return FamilyMembership object
     */
    public static FamilyMembership fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.readValue(json, FamilyMembership.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to FamilyMembership", e);
        }
    }

    /**
     * Convert this FamilyMembership object to XML string
     * 
     * @return XML representation of the FamilyMembership
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(FamilyMembership.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting FamilyMembership to XML", e);
        }
    }

    /**
     * Create a FamilyMembership object from XML string
     * 
     * @param xml XML string representation of FamilyMembership
     * @return FamilyMembership object
     */
    public static FamilyMembership fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(FamilyMembership.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (FamilyMembership) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to FamilyMembership", e);
        }
    }
} 