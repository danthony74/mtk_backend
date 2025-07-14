package com.mindthekid.models;

import java.util.List;

/**
 * Gender entity for user gender classification in the MTK Backend system.
 * 
 * This entity provides reference data for categorizing users by gender
 * and maintains relationships with users who have this gender classification.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class Gender {
    private Integer id;
    private String genderDesc;
    private String genderCode;
    
    // Relationships
    private List<User> users;

    /**
     * Default constructor
     */
    public Gender() {}

    /**
     * Constructor with required fields
     * 
     * @param genderDesc Human-readable gender description
     * @param genderCode Unique gender code
     */
    public Gender(String genderDesc, String genderCode) {
        this.genderDesc = genderDesc;
        this.genderCode = genderCode;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param genderDesc Human-readable gender description
     * @param genderCode Unique gender code
     */
    public Gender(Integer id, String genderDesc, String genderCode) {
        this.id = id;
        this.genderDesc = genderDesc;
        this.genderCode = genderCode;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getGenderDesc() { return genderDesc; }
    public void setGenderDesc(String genderDesc) { this.genderDesc = genderDesc; }

    public String getGenderCode() { return genderCode; }
    public void setGenderCode(String genderCode) { this.genderCode = genderCode; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    /**
     * Check if this gender has associated users
     * 
     * @return true if gender has users, false otherwise
     */
    public boolean hasUsers() {
        return users != null && !users.isEmpty();
    }

    /**
     * Get the number of users with this gender
     * 
     * @return Number of users, 0 if no users
     */
    public int getUserCount() {
        return users != null ? users.size() : 0;
    }

    @Override
    public String toString() {
        return String.format("Gender{id=%d, genderDesc='%s', genderCode='%s'}", 
            id, genderDesc, genderCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Gender gender = (Gender) obj;
        return id != null ? id.equals(gender.id) : gender.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (genderDesc != null ? genderDesc.hashCode() : 0);
        result = prime * result + (genderCode != null ? genderCode.hashCode() : 0);
        return result;
    }

    /**
     * Convert this Gender object to JSON string
     * 
     * @return JSON representation of the Gender
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Gender to JSON", e);
        }
    }

    /**
     * Create a Gender object from JSON string
     * 
     * @param json JSON string representation of Gender
     * @return Gender object
     */
    public static Gender fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, Gender.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to Gender", e);
        }
    }

    /**
     * Convert this Gender object to XML string
     * 
     * @return XML representation of the Gender
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Gender.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting Gender to XML", e);
        }
    }

    /**
     * Create a Gender object from XML string
     * 
     * @param xml XML string representation of Gender
     * @return Gender object
     */
    public static Gender fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Gender.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (Gender) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to Gender", e);
        }
    }
} 