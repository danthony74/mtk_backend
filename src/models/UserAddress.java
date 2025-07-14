package com.mindthekid.models;

import java.time.LocalDate;
import java.util.Optional;

/**
 * UserAddress entity representing the many-to-many relationship between users and addresses.
 * 
 * This junction entity connects users with their addresses and includes additional
 * metadata such as address type, start date, and optional end date for tracking
 * when addresses become active or inactive.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class UserAddress {
    private User user;
    private Address address;
    private AddressType addressType;
    private LocalDate startDate;
    private Optional<LocalDate> endDate;

    /**
     * Default constructor
     */
    public UserAddress() {}

    /**
     * Constructor with required fields
     * 
     * @param user Associated user
     * @param address Associated address
     * @param addressType Type of address (home, work, etc.)
     * @param startDate When this address became active
     */
    public UserAddress(User user, Address address, AddressType addressType, LocalDate startDate) {
        this.user = user;
        this.address = address;
        this.addressType = addressType;
        this.startDate = startDate;
    }

    /**
     * Constructor with all fields
     * 
     * @param user Associated user
     * @param address Associated address
     * @param addressType Type of address (home, work, etc.)
     * @param startDate When this address became active
     * @param endDate When this address became inactive (optional)
     */
    public UserAddress(User user, Address address, AddressType addressType, 
                      LocalDate startDate, Optional<LocalDate> endDate) {
        this.user = user;
        this.address = address;
        this.addressType = addressType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public AddressType getAddressType() { return addressType; }
    public void setAddressType(AddressType addressType) { this.addressType = addressType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public Optional<LocalDate> getEndDate() { return endDate; }
    public void setEndDate(Optional<LocalDate> endDate) { this.endDate = endDate; }

    /**
     * Check if this user address relationship is currently active
     * 
     * @return true if the relationship is active, false otherwise
     */
    public boolean isActive() {
        return endDate == null || !endDate.isPresent();
    }

    /**
     * Check if this user address relationship is active on a specific date
     * 
     * @param date The date to check
     * @return true if the relationship is active on the specified date, false otherwise
     */
    public boolean isActiveOn(LocalDate date) {
        if (date == null) return false;
        
        // Check if date is after or equal to start date
        if (date.isBefore(startDate)) {
            return false;
        }
        
        // Check if date is before end date (if end date exists)
        if (endDate != null && endDate.isPresent() && !date.isBefore(endDate.get())) {
            return false;
        }
        
        return true;
    }

    /**
     * Get the duration of this address relationship in days
     * 
     * @return Number of days, or null if still active
     */
    public Long getDurationInDays() {
        if (endDate == null || !endDate.isPresent()) {
            return null; // Still active
        }
        
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate.get());
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
     * Get the address ID
     * 
     * @return Address ID, null if address is not set
     */
    public Integer getAddressId() {
        return address != null ? address.getId() : null;
    }

    /**
     * Get the address type code
     * 
     * @return Address type code, null if address type is not set
     */
    public String getAddressTypeCode() {
        return addressType != null ? addressType.getAddressTypeCode() : null;
    }

    /**
     * Get the address type description
     * 
     * @return Address type description, null if address type is not set
     */
    public String getAddressTypeDesc() {
        return addressType != null ? addressType.getAddressTypeDesc() : null;
    }

    /**
     * Get the full address as a formatted string
     * 
     * @return Formatted address string, null if address is not set
     */
    public String getFullAddress() {
        return address != null ? address.getFullAddress() : null;
    }

    /**
     * Get the address summary
     * 
     * @return Address summary string, null if address is not set
     */
    public String getAddressSummary() {
        return address != null ? address.getAddressSummary() : null;
    }

    /**
     * Check if this is a home address
     * 
     * @return true if this is a home address, false otherwise
     */
    public boolean isHomeAddress() {
        return addressType != null && addressType.isHomeAddress();
    }

    /**
     * Check if this is a work address
     * 
     * @return true if this is a work address, false otherwise
     */
    public boolean isWorkAddress() {
        return addressType != null && addressType.isWorkAddress();
    }

    /**
     * Check if this is a billing address
     * 
     * @return true if this is a billing address, false otherwise
     */
    public boolean isBillingAddress() {
        return addressType != null && addressType.isBillingAddress();
    }

    /**
     * Check if this is a shipping address
     * 
     * @return true if this is a shipping address, false otherwise
     */
    public boolean isShippingAddress() {
        return addressType != null && addressType.isShippingAddress();
    }

    /**
     * End this address relationship
     * 
     * @param endDate The date when the relationship ended
     */
    public void endRelationship(LocalDate endDate) {
        this.endDate = Optional.of(endDate);
    }

    /**
     * Reactivate this address relationship
     */
    public void reactivate() {
        this.endDate = Optional.empty();
    }

    @Override
    public String toString() {
        return String.format("UserAddress{user='%s', address='%s', addressType='%s', startDate=%s, active=%s}", 
            user != null ? user.getId() : "null", 
            address != null ? address.getId() : "null",
            addressType != null ? addressType.getAddressTypeCode() : "null",
            startDate,
            isActive());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UserAddress that = (UserAddress) obj;
        
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        return startDate != null ? startDate.equals(that.startDate) : that.startDate == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (user != null ? user.hashCode() : 0);
        result = prime * result + (address != null ? address.hashCode() : 0);
        result = prime * result + (addressType != null ? addressType.hashCode() : 0);
        result = prime * result + (isDefault ? 1231 : 1237);
        result = prime * result + (isActive ? 1231 : 1237);
        return result;
    }

    /**
     * Convert this UserAddress object to JSON string
     * 
     * @return JSON representation of the UserAddress
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting UserAddress to JSON", e);
        }
    }

    /**
     * Create a UserAddress object from JSON string
     * 
     * @param json JSON string representation of UserAddress
     * @return UserAddress object
     */
    public static UserAddress fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, UserAddress.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to UserAddress", e);
        }
    }

    /**
     * Convert this UserAddress object to XML string
     * 
     * @return XML representation of the UserAddress
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(UserAddress.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting UserAddress to XML", e);
        }
    }

    /**
     * Create a UserAddress object from XML string
     * 
     * @param xml XML string representation of UserAddress
     * @return UserAddress object
     */
    public static UserAddress fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(UserAddress.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (UserAddress) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to UserAddress", e);
        }
    }
} 