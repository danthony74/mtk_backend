package com.mindthekid.models;

import java.util.List;
import java.util.Optional;

/**
 * User entity representing the core user in the MTK Backend system.
 * 
 * This is the central entity that connects to all other entities through
 * various relationships including addresses, families, subscriptions, and sharing.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class User {
    private String id;
    private String email;
    private String firstName;
    private Optional<String> middleNameInitial;
    private String familyName;
    private String phone;
    private Gender gender;
    private String userHash;
    
    // Relationships
    private List<UserAddress> addresses;
    private List<FamilyMembership> familyMemberships;
    private List<UserSubscription> subscriptions;
    private List<Family> createdFamilies;
    private List<UserShare> sharedBy;
    private List<UserShare> sharedWith;

    /**
     * Default constructor
     */
    public User() {}

    /**
     * Constructor with required fields
     * 
     * @param id Unique user identifier
     * @param email User's email address
     * @param firstName User's first name
     * @param familyName User's family name
     * @param phone User's phone number
     * @param gender User's gender classification
     * @param userHash Hashed user credentials
     */
    public User(String id, String email, String firstName, String familyName, 
               String phone, Gender gender, String userHash) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.familyName = familyName;
        this.phone = phone;
        this.gender = gender;
        this.userHash = userHash;
    }

    /**
     * Constructor with all fields including optional middle name
     * 
     * @param id Unique user identifier
     * @param email User's email address
     * @param firstName User's first name
     * @param middleNameInitial Optional middle name initial
     * @param familyName User's family name
     * @param phone User's phone number
     * @param gender User's gender classification
     * @param userHash Hashed user credentials
     */
    public User(String id, String email, String firstName, Optional<String> middleNameInitial,
               String familyName, String phone, Gender gender, String userHash) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.middleNameInitial = middleNameInitial;
        this.familyName = familyName;
        this.phone = phone;
        this.gender = gender;
        this.userHash = userHash;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public Optional<String> getMiddleNameInitial() { return middleNameInitial; }
    public void setMiddleNameInitial(Optional<String> middleNameInitial) { this.middleNameInitial = middleNameInitial; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getUserHash() { return userHash; }
    public void setUserHash(String userHash) { this.userHash = userHash; }

    // Relationship getters and setters
    public List<UserAddress> getAddresses() { return addresses; }
    public void setAddresses(List<UserAddress> addresses) { this.addresses = addresses; }

    public List<FamilyMembership> getFamilyMemberships() { return familyMemberships; }
    public void setFamilyMemberships(List<FamilyMembership> familyMemberships) { this.familyMemberships = familyMemberships; }

    public List<UserSubscription> getSubscriptions() { return subscriptions; }
    public void setSubscriptions(List<UserSubscription> subscriptions) { this.subscriptions = subscriptions; }

    public List<Family> getCreatedFamilies() { return createdFamilies; }
    public void setCreatedFamilies(List<Family> createdFamilies) { this.createdFamilies = createdFamilies; }

    public List<UserShare> getSharedBy() { return sharedBy; }
    public void setSharedBy(List<UserShare> sharedBy) { this.sharedBy = sharedBy; }

    public List<UserShare> getSharedWith() { return sharedWith; }
    public void setSharedWith(List<UserShare> sharedWith) { this.sharedWith = sharedWith; }

    /**
     * Get the user's full name
     * 
     * @return Full name including middle initial if present
     */
    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName);
        
        if (middleNameInitial != null && middleNameInitial.isPresent()) {
            fullName.append(" ").append(middleNameInitial.get());
        }
        
        fullName.append(" ").append(familyName);
        return fullName.toString();
    }

    /**
     * Get the user's display name (first name + family name)
     * 
     * @return Display name without middle initial
     */
    public String getDisplayName() {
        return firstName + " " + familyName;
    }

    /**
     * Check if user has active addresses
     * 
     * @return true if user has addresses, false otherwise
     */
    public boolean hasAddresses() {
        return addresses != null && !addresses.isEmpty();
    }

    /**
     * Check if user has active family memberships
     * 
     * @return true if user has family memberships, false otherwise
     */
    public boolean hasFamilyMemberships() {
        return familyMemberships != null && !familyMemberships.isEmpty();
    }

    /**
     * Check if user has active subscriptions
     * 
     * @return true if user has subscriptions, false otherwise
     */
    public boolean hasSubscriptions() {
        return subscriptions != null && !subscriptions.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', email='%s', firstName='%s', familyName='%s'}", 
            id, email, firstName, familyName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        return id != null ? id.equals(user.id) : user.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (email != null ? email.hashCode() : 0);
        result = prime * result + (displayName != null ? displayName.hashCode() : 0);
        result = prime * result + (firstName != null ? firstName.hashCode() : 0);
        result = prime * result + (lastName != null ? lastName.hashCode() : 0);
        result = prime * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = prime * result + (gender != null ? gender.hashCode() : 0);
        result = prime * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = prime * result + (isActive ? 1231 : 1237);
        result = prime * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = prime * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }

    /**
     * Convert this User object to JSON string
     * 
     * @return JSON representation of the User
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting User to JSON", e);
        }
    }

    /**
     * Create a User object from JSON string
     * 
     * @param json JSON string representation of User
     * @return User object
     */
    public static User fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.readValue(json, User.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to User", e);
        }
    }

    /**
     * Convert this User object to XML string
     * 
     * @return XML representation of the User
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(User.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting User to XML", e);
        }
    }

    /**
     * Create a User object from XML string
     * 
     * @param xml XML string representation of User
     * @return User object
     */
    public static User fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(User.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (User) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to User", e);
        }
    }
} 