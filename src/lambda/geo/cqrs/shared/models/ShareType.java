package com.mindthekid.geo.cqrs.shared.models;

import java.util.List;

/**
 * ShareType entity for categorizing sharing relationships in the MTK Backend system.
 * 
 * This entity provides reference data for categorizing sharing relationships
 * between users (e.g., family sharing, friend sharing, business sharing).
 * It maintains relationships with user shares that use this type.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class ShareType {
    private Integer id;
    private String shareTypeDesc;
    private String shareTypeCode;
    
    // Relationships
    private List<UserShare> userShares;

    /**
     * Default constructor
     */
    public ShareType() {}

    /**
     * Constructor with required fields
     * 
     * @param shareTypeDesc Human-readable share type description
     * @param shareTypeCode Unique share type code
     */
    public ShareType(String shareTypeDesc, String shareTypeCode) {
        this.shareTypeDesc = shareTypeDesc;
        this.shareTypeCode = shareTypeCode;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param shareTypeDesc Human-readable share type description
     * @param shareTypeCode Unique share type code
     */
    public ShareType(Integer id, String shareTypeDesc, String shareTypeCode) {
        this.id = id;
        this.shareTypeDesc = shareTypeDesc;
        this.shareTypeCode = shareTypeCode;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getShareTypeDesc() { return shareTypeDesc; }
    public void setShareTypeDesc(String shareTypeDesc) { this.shareTypeDesc = shareTypeDesc; }

    public String getShareTypeCode() { return shareTypeCode; }
    public void setShareTypeCode(String shareTypeCode) { this.shareTypeCode = shareTypeCode; }

    public List<UserShare> getUserShares() { return userShares; }
    public void setUserShares(List<UserShare> userShares) { this.userShares = userShares; }

    /**
     * Check if this share type has associated user shares
     * 
     * @return true if share type has user shares, false otherwise
     */
    public boolean hasUserShares() {
        return userShares != null && !userShares.isEmpty();
    }

    /**
     * Get the number of user shares using this type
     * 
     * @return Number of user shares, 0 if none
     */
    public int getUserShareCount() {
        return userShares != null ? userShares.size() : 0;
    }

    /**
     * Check if this is a family sharing type
     * 
     * @return true if this is a family sharing type, false otherwise
     */
    public boolean isFamilySharing() {
        return "FAMILY".equalsIgnoreCase(shareTypeCode) || 
               "FAMILY_SHARING".equalsIgnoreCase(shareTypeCode) ||
               shareTypeDesc != null && shareTypeDesc.toLowerCase().contains("family");
    }

    /**
     * Check if this is a friend sharing type
     * 
     * @return true if this is a friend sharing type, false otherwise
     */
    public boolean isFriendSharing() {
        return "FRIEND".equalsIgnoreCase(shareTypeCode) || 
               "FRIEND_SHARING".equalsIgnoreCase(shareTypeCode) ||
               shareTypeDesc != null && shareTypeDesc.toLowerCase().contains("friend");
    }

    /**
     * Check if this is a business sharing type
     * 
     * @return true if this is a business sharing type, false otherwise
     */
    public boolean isBusinessSharing() {
        return "BUSINESS".equalsIgnoreCase(shareTypeCode) || 
               "BUSINESS_SHARING".equalsIgnoreCase(shareTypeCode) ||
               shareTypeDesc != null && shareTypeDesc.toLowerCase().contains("business");
    }

    /**
     * Check if this is a public sharing type
     * 
     * @return true if this is a public sharing type, false otherwise
     */
    public boolean isPublicSharing() {
        return "PUBLIC".equalsIgnoreCase(shareTypeCode) || 
               "PUBLIC_SHARING".equalsIgnoreCase(shareTypeCode) ||
               shareTypeDesc != null && shareTypeDesc.toLowerCase().contains("public");
    }

    /**
     * Check if this is a private sharing type
     * 
     * @return true if this is a private sharing type, false otherwise
     */
    public boolean isPrivateSharing() {
        return "PRIVATE".equalsIgnoreCase(shareTypeCode) || 
               "PRIVATE_SHARING".equalsIgnoreCase(shareTypeCode) ||
               shareTypeDesc != null && shareTypeDesc.toLowerCase().contains("private");
    }

    /**
     * Check if this is a temporary sharing type
     * 
     * @return true if this is a temporary sharing type, false otherwise
     */
    public boolean isTemporarySharing() {
        return "TEMPORARY".equalsIgnoreCase(shareTypeCode) || 
               "TEMP".equalsIgnoreCase(shareTypeCode) ||
               shareTypeDesc != null && shareTypeDesc.toLowerCase().contains("temporary");
    }

    /**
     * Check if this is a permanent sharing type
     * 
     * @return true if this is a permanent sharing type, false otherwise
     */
    public boolean isPermanentSharing() {
        return "PERMANENT".equalsIgnoreCase(shareTypeCode) || 
               "PERM".equalsIgnoreCase(shareTypeCode) ||
               shareTypeDesc != null && shareTypeDesc.toLowerCase().contains("permanent");
    }

    /**
     * Check if this sharing type requires approval
     * 
     * @return true if this sharing type requires approval, false otherwise
     */
    public boolean requiresApproval() {
        return isBusinessSharing() || isPublicSharing() || 
               "APPROVAL_REQUIRED".equalsIgnoreCase(shareTypeCode);
    }

    /**
     * Check if this sharing type is read-only
     * 
     * @return true if this sharing type is read-only, false otherwise
     */
    public boolean isReadOnly() {
        return "READ_ONLY".equalsIgnoreCase(shareTypeCode) || 
               shareTypeDesc != null && shareTypeDesc.toLowerCase().contains("read only");
    }

    /**
     * Check if this sharing type allows full access
     * 
     * @return true if this sharing type allows full access, false otherwise
     */
    public boolean allowsFullAccess() {
        return isFamilySharing() || isPrivateSharing() || 
               "FULL_ACCESS".equalsIgnoreCase(shareTypeCode);
    }

    @Override
    public String toString() {
        return String.format("ShareType{id=%d, shareTypeDesc='%s', shareTypeCode='%s'}", 
            id, shareTypeDesc, shareTypeCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ShareType that = (ShareType) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (shareTypeDesc != null ? shareTypeDesc.hashCode() : 0);
        result = prime * result + (shareTypeCode != null ? shareTypeCode.hashCode() : 0);
        return result;
    }

    /**
     * Convert this ShareType object to JSON string
     * 
     * @return JSON representation of the ShareType
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting ShareType to JSON", e);
        }
    }

    /**
     * Create a ShareType object from JSON string
     * 
     * @param json JSON string representation of ShareType
     * @return ShareType object
     */
    public static ShareType fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, ShareType.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to ShareType", e);
        }
    }

    /**
     * Convert this ShareType object to XML string
     * 
     * @return XML representation of the ShareType
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(ShareType.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting ShareType to XML", e);
        }
    }

    /**
     * Create a ShareType object from XML string
     * 
     * @param xml XML string representation of ShareType
     * @return ShareType object
     */
    public static ShareType fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(ShareType.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (ShareType) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to ShareType", e);
        }
    }
} 