package com.mindthekid.models;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Family entity representing family groups in the MTK Backend system.
 * 
 * This entity represents family units with a creator, family name, and
 * temporal information. It maintains relationships with family memberships
 * that connect users to the family.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class Family {
    private Integer id;
    private User creator;
    private String familyName;
    private LocalDate dateStarted;
    private Optional<LocalDate> dateEnded;
    
    // Relationships
    private List<FamilyMembership> memberships;

    /**
     * Default constructor
     */
    public Family() {}

    /**
     * Constructor with required fields
     * 
     * @param creator User who created the family
     * @param familyName Name of the family
     * @param dateStarted When the family was created
     */
    public Family(User creator, String familyName, LocalDate dateStarted) {
        this.creator = creator;
        this.familyName = familyName;
        this.dateStarted = dateStarted;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param creator User who created the family
     * @param familyName Name of the family
     * @param dateStarted When the family was created
     * @param dateEnded When the family was dissolved (optional)
     */
    public Family(Integer id, User creator, String familyName, LocalDate dateStarted, Optional<LocalDate> dateEnded) {
        this.id = id;
        this.creator = creator;
        this.familyName = familyName;
        this.dateStarted = dateStarted;
        this.dateEnded = dateEnded;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public LocalDate getDateStarted() { return dateStarted; }
    public void setDateStarted(LocalDate dateStarted) { this.dateStarted = dateStarted; }

    public Optional<LocalDate> getDateEnded() { return dateEnded; }
    public void setDateEnded(Optional<LocalDate> dateEnded) { this.dateEnded = dateEnded; }

    public List<FamilyMembership> getMemberships() { return memberships; }
    public void setMemberships(List<FamilyMembership> memberships) { this.memberships = memberships; }

    /**
     * Check if this family is currently active
     * 
     * @return true if the family is active, false otherwise
     */
    public boolean isActive() {
        return dateEnded == null || !dateEnded.isPresent();
    }

    /**
     * Check if this family is active on a specific date
     * 
     * @param date The date to check
     * @return true if the family is active on the specified date, false otherwise
     */
    public boolean isActiveOn(LocalDate date) {
        if (date == null) return false;
        
        // Check if date is after or equal to start date
        if (date.isBefore(dateStarted)) {
            return false;
        }
        
        // Check if date is before end date (if end date exists)
        if (dateEnded != null && dateEnded.isPresent() && !date.isBefore(dateEnded.get())) {
            return false;
        }
        
        return true;
    }

    /**
     * Get the creator ID
     * 
     * @return Creator ID, null if creator is not set
     */
    public String getCreatorId() {
        return creator != null ? creator.getId() : null;
    }

    /**
     * Get the creator's name
     * 
     * @return Creator's display name, null if creator is not set
     */
    public String getCreatorName() {
        return creator != null ? creator.getDisplayName() : null;
    }

    /**
     * Check if this family has members
     * 
     * @return true if family has members, false otherwise
     */
    public boolean hasMembers() {
        return memberships != null && !memberships.isEmpty();
    }

    /**
     * Get the number of family members
     * 
     * @return Number of members, 0 if no members
     */
    public int getMemberCount() {
        return memberships != null ? memberships.size() : 0;
    }

    /**
     * Get active family members (memberships that haven't ended)
     * 
     * @return List of active family memberships
     */
    public List<FamilyMembership> getActiveMembers() {
        if (memberships == null) {
            return List.of();
        }
        
        return memberships.stream()
            .filter(FamilyMembership::isActive)
            .toList();
    }

    /**
     * Get the number of active family members
     * 
     * @return Number of active members, 0 if no active members
     */
    public int getActiveMemberCount() {
        return getActiveMembers().size();
    }

    /**
     * Get family members by type
     * 
     * @param memberTypeCode The member type code to filter by
     * @return List of family memberships with the specified type
     */
    public List<FamilyMembership> getMembersByType(String memberTypeCode) {
        if (memberships == null) {
            return List.of();
        }
        
        return memberships.stream()
            .filter(membership -> membership.getMembershipType() != null && 
                                memberTypeCode.equals(membership.getMembershipType().getMemberTypeCode()))
            .toList();
    }

    /**
     * Get the duration of this family in days
     * 
     * @return Number of days, or null if still active
     */
    public Long getDurationInDays() {
        if (dateEnded == null || !dateEnded.isPresent()) {
            return null; // Still active
        }
        
        return java.time.temporal.ChronoUnit.DAYS.between(dateStarted, dateEnded.get());
    }

    /**
     * End this family
     * 
     * @param endDate The date when the family ended
     */
    public void endFamily(LocalDate endDate) {
        this.dateEnded = Optional.of(endDate);
    }

    /**
     * Reactivate this family
     */
    public void reactivate() {
        this.dateEnded = Optional.empty();
    }

    @Override
    public String toString() {
        return String.format("Family{id=%d, familyName='%s', creator='%s', active=%s}", 
            id, familyName, creator != null ? creator.getId() : "null", isActive());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Family family = (Family) obj;
        return id != null ? id.equals(family.id) : family.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (familyName != null ? familyName.hashCode() : 0);
        result = prime * result + (familyCode != null ? familyCode.hashCode() : 0);
        result = prime * result + (isActive ? 1231 : 1237);
        result = prime * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = prime * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }

    /**
     * Convert this Family object to JSON string
     * 
     * @return JSON representation of the Family
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Family to JSON", e);
        }
    }

    /**
     * Create a Family object from JSON string
     * 
     * @param json JSON string representation of Family
     * @return Family object
     */
    public static Family fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.readValue(json, Family.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to Family", e);
        }
    }

    /**
     * Convert this Family object to XML string
     * 
     * @return XML representation of the Family
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Family.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting Family to XML", e);
        }
    }

    /**
     * Create a Family object from XML string
     * 
     * @param xml XML string representation of Family
     * @return Family object
     */
    public static Family fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Family.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (Family) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to Family", e);
        }
    }
} 