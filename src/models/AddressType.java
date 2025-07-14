package com.mindthekid.models;

import java.util.List;

/**
 * AddressType entity for categorizing address types in the MTK Backend system.
 * 
 * This entity provides reference data for categorizing addresses by their purpose
 * or usage (e.g., home, work, billing, shipping). It maintains relationships
 * with user addresses that use this type.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class AddressType {
    private Integer id;
    private String addressTypeDesc;
    private String addressTypeCode;
    
    // Relationships
    private List<UserAddress> userAddresses;

    /**
     * Default constructor
     */
    public AddressType() {}

    /**
     * Constructor with required fields
     * 
     * @param addressTypeDesc Human-readable address type description
     * @param addressTypeCode Unique address type code
     */
    public AddressType(String addressTypeDesc, String addressTypeCode) {
        this.addressTypeDesc = addressTypeDesc;
        this.addressTypeCode = addressTypeCode;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param addressTypeDesc Human-readable address type description
     * @param addressTypeCode Unique address type code
     */
    public AddressType(Integer id, String addressTypeDesc, String addressTypeCode) {
        this.id = id;
        this.addressTypeDesc = addressTypeDesc;
        this.addressTypeCode = addressTypeCode;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getAddressTypeDesc() { return addressTypeDesc; }
    public void setAddressTypeDesc(String addressTypeDesc) { this.addressTypeDesc = addressTypeDesc; }

    public String getAddressTypeCode() { return addressTypeCode; }
    public void setAddressTypeCode(String addressTypeCode) { this.addressTypeCode = addressTypeCode; }

    public List<UserAddress> getUserAddresses() { return userAddresses; }
    public void setUserAddresses(List<UserAddress> userAddresses) { this.userAddresses = userAddresses; }

    /**
     * Check if this address type has associated user addresses
     * 
     * @return true if address type has user addresses, false otherwise
     */
    public boolean hasUserAddresses() {
        return userAddresses != null && !userAddresses.isEmpty();
    }

    /**
     * Get the number of user addresses using this type
     * 
     * @return Number of user addresses, 0 if none
     */
    public int getUserAddressCount() {
        return userAddresses != null ? userAddresses.size() : 0;
    }

    /**
     * Check if this is a home address type
     * 
     * @return true if this is a home address type, false otherwise
     */
    public boolean isHomeAddress() {
        return "HOME".equalsIgnoreCase(addressTypeCode) || 
               "RESIDENTIAL".equalsIgnoreCase(addressTypeCode);
    }

    /**
     * Check if this is a work address type
     * 
     * @return true if this is a work address type, false otherwise
     */
    public boolean isWorkAddress() {
        return "WORK".equalsIgnoreCase(addressTypeCode) || 
               "BUSINESS".equalsIgnoreCase(addressTypeCode) ||
               "OFFICE".equalsIgnoreCase(addressTypeCode);
    }

    /**
     * Check if this is a billing address type
     * 
     * @return true if this is a billing address type, false otherwise
     */
    public boolean isBillingAddress() {
        return "BILLING".equalsIgnoreCase(addressTypeCode) || 
               "INVOICE".equalsIgnoreCase(addressTypeCode);
    }

    /**
     * Check if this is a shipping address type
     * 
     * @return true if this is a shipping address type, false otherwise
     */
    public boolean isShippingAddress() {
        return "SHIPPING".equalsIgnoreCase(addressTypeCode) || 
               "DELIVERY".equalsIgnoreCase(addressTypeCode);
    }

    @Override
    public String toString() {
        return String.format("AddressType{id=%d, addressTypeDesc='%s', addressTypeCode='%s'}", 
            id, addressTypeDesc, addressTypeCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        AddressType that = (AddressType) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (addressTypeDesc != null ? addressTypeDesc.hashCode() : 0);
        result = prime * result + (addressTypeCode != null ? addressTypeCode.hashCode() : 0);
        return result;
    }

    /**
     * Convert this AddressType object to JSON string
     * 
     * @return JSON representation of the AddressType
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting AddressType to JSON", e);
        }
    }

    /**
     * Create an AddressType object from JSON string
     * 
     * @param json JSON string representation of AddressType
     * @return AddressType object
     */
    public static AddressType fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, AddressType.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to AddressType", e);
        }
    }

    /**
     * Convert this AddressType object to XML string
     * 
     * @return XML representation of the AddressType
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(AddressType.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting AddressType to XML", e);
        }
    }

    /**
     * Create an AddressType object from XML string
     * 
     * @param xml XML string representation of AddressType
     * @return AddressType object
     */
    public static AddressType fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(AddressType.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (AddressType) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to AddressType", e);
        }
    }
} 