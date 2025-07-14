package com.mindthekid.models;

import java.util.List;
import java.util.Optional;

/**
 * Address entity for physical address data in the MTK Backend system.
 * 
 * This entity represents physical addresses with geographical data including
 * address lines, country, and optional state/province information. It maintains
 * relationships with users through the UserAddress junction entity.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class Address {
    private Integer id;
    private String addressLineOne;
    private String addressLineTwo;
    private Optional<String> addressLineThree;
    private Country country;
    private Optional<CountryState> state;
    
    // Relationships
    private List<UserAddress> userAddresses;

    /**
     * Default constructor
     */
    public Address() {}

    /**
     * Constructor with required fields
     * 
     * @param addressLineOne Primary address line
     * @param addressLineTwo Secondary address line
     * @param country Associated country
     */
    public Address(String addressLineOne, String addressLineTwo, Country country) {
        this.addressLineOne = addressLineOne;
        this.addressLineTwo = addressLineTwo;
        this.country = country;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param addressLineOne Primary address line
     * @param addressLineTwo Secondary address line
     * @param addressLineThree Optional tertiary address line
     * @param country Associated country
     * @param state Optional associated state/province
     */
    public Address(Integer id, String addressLineOne, String addressLineTwo, 
                  Optional<String> addressLineThree, Country country, Optional<CountryState> state) {
        this.id = id;
        this.addressLineOne = addressLineOne;
        this.addressLineTwo = addressLineTwo;
        this.addressLineThree = addressLineThree;
        this.country = country;
        this.state = state;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getAddressLineOne() { return addressLineOne; }
    public void setAddressLineOne(String addressLineOne) { this.addressLineOne = addressLineOne; }

    public String getAddressLineTwo() { return addressLineTwo; }
    public void setAddressLineTwo(String addressLineTwo) { this.addressLineTwo = addressLineTwo; }

    public Optional<String> getAddressLineThree() { return addressLineThree; }
    public void setAddressLineThree(Optional<String> addressLineThree) { this.addressLineThree = addressLineThree; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public Optional<CountryState> getState() { return state; }
    public void setState(Optional<CountryState> state) { this.state = state; }

    public List<UserAddress> getUserAddresses() { return userAddresses; }
    public void setUserAddresses(List<UserAddress> userAddresses) { this.userAddresses = userAddresses; }

    /**
     * Check if this address has associated users
     * 
     * @return true if address has users, false otherwise
     */
    public boolean hasUsers() {
        return userAddresses != null && !userAddresses.isEmpty();
    }

    /**
     * Get the number of users associated with this address
     * 
     * @return Number of users, 0 if no users
     */
    public int getUserCount() {
        return userAddresses != null ? userAddresses.size() : 0;
    }

    /**
     * Get the country name
     * 
     * @return Country name, null if country is not set
     */
    public String getCountryName() {
        return country != null ? country.getCountryName() : null;
    }

    /**
     * Get the country code
     * 
     * @return Country code, null if country is not set
     */
    public String getCountryCode() {
        return country != null ? country.getCountryCode() : null;
    }

    /**
     * Get the state name
     * 
     * @return State name, null if state is not set
     */
    public String getStateName() {
        return state != null && state.isPresent() ? state.get().getStateName() : null;
    }

    /**
     * Get the state code
     * 
     * @return State code, null if state is not set
     */
    public String getStateCode() {
        return state != null && state.isPresent() ? state.get().getStateCode() : null;
    }

    /**
     * Get the full address as a formatted string
     * 
     * @return Formatted address string
     */
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        
        // Add address lines
        fullAddress.append(addressLineOne);
        
        if (addressLineTwo != null && !addressLineTwo.trim().isEmpty()) {
            fullAddress.append(", ").append(addressLineTwo);
        }
        
        if (addressLineThree != null && addressLineThree.isPresent() && 
            !addressLineThree.get().trim().isEmpty()) {
            fullAddress.append(", ").append(addressLineThree.get());
        }
        
        // Add state if present
        if (state != null && state.isPresent()) {
            fullAddress.append(", ").append(state.get().getStateName());
        }
        
        // Add country
        if (country != null) {
            fullAddress.append(", ").append(country.getCountryName());
        }
        
        return fullAddress.toString();
    }

    /**
     * Get the address summary (first line + location)
     * 
     * @return Address summary string
     */
    public String getAddressSummary() {
        StringBuilder summary = new StringBuilder(addressLineOne);
        
        if (state != null && state.isPresent()) {
            summary.append(", ").append(state.get().getStateName());
        }
        
        if (country != null) {
            summary.append(", ").append(country.getCountryName());
        }
        
        return summary.toString();
    }

    /**
     * Check if this address is in a specific country
     * 
     * @param countryCode The country code to check
     * @return true if address is in the specified country, false otherwise
     */
    public boolean isInCountry(String countryCode) {
        return country != null && countryCode.equals(country.getCountryCode());
    }

    /**
     * Check if this address is in a specific state
     * 
     * @param stateCode The state code to check
     * @return true if address is in the specified state, false otherwise
     */
    public boolean isInState(String stateCode) {
        return state != null && state.isPresent() && stateCode.equals(state.get().getStateCode());
    }

    @Override
    public String toString() {
        return String.format("Address{id=%d, addressLineOne='%s', country='%s'}", 
            id, addressLineOne, country != null ? country.getCountryName() : "null");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Address address = (Address) obj;
        return id != null ? id.equals(address.id) : address.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (streetAddress != null ? streetAddress.hashCode() : 0);
        result = prime * result + (city != null ? city.hashCode() : 0);
        result = prime * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = prime * result + (country != null ? country.hashCode() : 0);
        result = prime * result + (countryState != null ? countryState.hashCode() : 0);
        result = prime * result + (isActive ? 1231 : 1237);
        return result;
    }

    /**
     * Convert this Address object to JSON string
     * 
     * @return JSON representation of the Address
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Address to JSON", e);
        }
    }

    /**
     * Create an Address object from JSON string
     * 
     * @param json JSON string representation of Address
     * @return Address object
     */
    public static Address fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, Address.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to Address", e);
        }
    }

    /**
     * Convert this Address object to XML string
     * 
     * @return XML representation of the Address
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Address.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting Address to XML", e);
        }
    }

    /**
     * Create an Address object from XML string
     * 
     * @param xml XML string representation of Address
     * @return Address object
     */
    public static Address fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Address.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (Address) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to Address", e);
        }
    }
} 