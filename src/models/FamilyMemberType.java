package com.mindthekid.models;

import java.util.List;

/**
 * FamilyMemberType entity for categorizing family member roles in the MTK Backend system.
 * 
 * This entity provides reference data for categorizing family members by their role
 * or relationship within the family (e.g., parent, child, guardian, sibling).
 * It maintains relationships with family memberships that use this type.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class FamilyMemberType {
    private Integer id;
    private String memberTypeDesc;
    private String memberTypeCode;
    
    // Relationships
    private List<FamilyMembership> memberships;

    /**
     * Default constructor
     */
    public FamilyMemberType() {}

    /**
     * Constructor with required fields
     * 
     * @param memberTypeDesc Human-readable member type description
     * @param memberTypeCode Unique member type code
     */
    public FamilyMemberType(String memberTypeDesc, String memberTypeCode) {
        this.memberTypeDesc = memberTypeDesc;
        this.memberTypeCode = memberTypeCode;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param memberTypeDesc Human-readable member type description
     * @param memberTypeCode Unique member type code
     */
    public FamilyMemberType(Integer id, String memberTypeDesc, String memberTypeCode) {
        this.id = id;
        this.memberTypeDesc = memberTypeDesc;
        this.memberTypeCode = memberTypeCode;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMemberTypeDesc() { return memberTypeDesc; }
    public void setMemberTypeDesc(String memberTypeDesc) { this.memberTypeDesc = memberTypeDesc; }

    public String getMemberTypeCode() { return memberTypeCode; }
    public void setMemberTypeCode(String memberTypeCode) { this.memberTypeCode = memberTypeCode; }

    public List<FamilyMembership> getMemberships() { return memberships; }
    public void setMemberships(List<FamilyMembership> memberships) { this.memberships = memberships; }

    /**
     * Check if this member type has associated memberships
     * 
     * @return true if member type has memberships, false otherwise
     */
    public boolean hasMemberships() {
        return memberships != null && !memberships.isEmpty();
    }

    /**
     * Get the number of memberships using this type
     * 
     * @return Number of memberships, 0 if none
     */
    public int getMembershipCount() {
        return memberships != null ? memberships.size() : 0;
    }

    /**
     * Check if this is a parent member type
     * 
     * @return true if this is a parent member type, false otherwise
     */
    public boolean isParent() {
        return "PARENT".equalsIgnoreCase(memberTypeCode) || 
               "FATHER".equalsIgnoreCase(memberTypeCode) ||
               "MOTHER".equalsIgnoreCase(memberTypeCode);
    }

    /**
     * Check if this is a child member type
     * 
     * @return true if this is a child member type, false otherwise
     */
    public boolean isChild() {
        return "CHILD".equalsIgnoreCase(memberTypeCode) || 
               "SON".equalsIgnoreCase(memberTypeCode) ||
               "DAUGHTER".equalsIgnoreCase(memberTypeCode);
    }

    /**
     * Check if this is a guardian member type
     * 
     * @return true if this is a guardian member type, false otherwise
     */
    public boolean isGuardian() {
        return "GUARDIAN".equalsIgnoreCase(memberTypeCode) || 
               "LEGAL_GUARDIAN".equalsIgnoreCase(memberTypeCode);
    }

    /**
     * Check if this is a sibling member type
     * 
     * @return true if this is a sibling member type, false otherwise
     */
    public boolean isSibling() {
        return "SIBLING".equalsIgnoreCase(memberTypeCode) || 
               "BROTHER".equalsIgnoreCase(memberTypeCode) ||
               "SISTER".equalsIgnoreCase(memberTypeCode);
    }

    /**
     * Check if this is an adult member type
     * 
     * @return true if this is an adult member type, false otherwise
     */
    public boolean isAdult() {
        return isParent() || isGuardian() || "ADULT".equalsIgnoreCase(memberTypeCode);
    }

    /**
     * Check if this is a minor member type
     * 
     * @return true if this is a minor member type, false otherwise
     */
    public boolean isMinor() {
        return isChild() || "MINOR".equalsIgnoreCase(memberTypeCode);
    }

    @Override
    public String toString() {
        return String.format("FamilyMemberType{id=%d, memberTypeDesc='%s', memberTypeCode='%s'}", 
            id, memberTypeDesc, memberTypeCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        FamilyMemberType that = (FamilyMemberType) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (memberTypeDesc != null ? memberTypeDesc.hashCode() : 0);
        result = prime * result + (memberTypeCode != null ? memberTypeCode.hashCode() : 0);
        return result;
    }

    /**
     * Convert this FamilyMemberType object to JSON string
     * 
     * @return JSON representation of the FamilyMemberType
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting FamilyMemberType to JSON", e);
        }
    }

    /**
     * Create a FamilyMemberType object from JSON string
     * 
     * @param json JSON string representation of FamilyMemberType
     * @return FamilyMemberType object
     */
    public static FamilyMemberType fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, FamilyMemberType.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to FamilyMemberType", e);
        }
    }

    /**
     * Convert this FamilyMemberType object to XML string
     * 
     * @return XML representation of the FamilyMemberType
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(FamilyMemberType.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting FamilyMemberType to XML", e);
        }
    }

    /**
     * Create a FamilyMemberType object from XML string
     * 
     * @param xml XML string representation of FamilyMemberType
     * @return FamilyMemberType object
     */
    public static FamilyMemberType fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(FamilyMemberType.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (FamilyMemberType) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to FamilyMemberType", e);
        }
    }
} 