package com.mindthekid.geo.cqrs.shared.models;

import java.util.List;

/**
 * Country entity for geographical data in the MTK Backend system.
 * 
 * This entity represents countries with their associated metadata including
 * phone prefixes, country codes, and encoding information. It maintains
 * relationships with states/provinces and addresses within the country.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class Country {
    private Integer id;
    private String countryName;
    private String encoding;
    private String phonePrefix;
    private String countryCode;
    
    // Relationships
    private List<CountryState> states;
    private List<Address> addresses;

    /**
     * Default constructor
     */
    public Country() {}

    /**
     * Constructor with required fields
     * 
     * @param countryName Name of the country
     * @param encoding Character encoding for the country
     * @param phonePrefix International phone prefix
     * @param countryCode ISO country code
     */
    public Country(String countryName, String encoding, String phonePrefix, String countryCode) {
        this.countryName = countryName;
        this.encoding = encoding;
        this.phonePrefix = phonePrefix;
        this.countryCode = countryCode;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param countryName Name of the country
     * @param encoding Character encoding for the country
     * @param phonePrefix International phone prefix
     * @param countryCode ISO country code
     */
    public Country(Integer id, String countryName, String encoding, String phonePrefix, String countryCode) {
        this.id = id;
        this.countryName = countryName;
        this.encoding = encoding;
        this.phonePrefix = phonePrefix;
        this.countryCode = countryCode;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public String getEncoding() { return encoding; }
    public void setEncoding(String encoding) { this.encoding = encoding; }

    public String getPhonePrefix() { return phonePrefix; }
    public void setPhonePrefix(String phonePrefix) { this.phonePrefix = phonePrefix; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public List<CountryState> getStates() { return states; }
    public void setStates(List<CountryState> states) { this.states = states; }

    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }

    /**
     * Check if this country has states/provinces
     * 
     * @return true if country has states, false otherwise
     */
    public boolean hasStates() {
        return states != null && !states.isEmpty();
    }

    /**
     * Check if this country has addresses
     * 
     * @return true if country has addresses, false otherwise
     */
    public boolean hasAddresses() {
        return addresses != null && !addresses.isEmpty();
    }

    /**
     * Get the number of states/provinces in this country
     * 
     * @return Number of states, 0 if no states
     */
    public int getStateCount() {
        return states != null ? states.size() : 0;
    }

    /**
     * Get the number of addresses in this country
     * 
     * @return Number of addresses, 0 if no addresses
     */
    public int getAddressCount() {
        return addresses != null ? addresses.size() : 0;
    }

    /**
     * Get a state by its code
     * 
     * @param stateCode The state code to search for
     * @return The state if found, null otherwise
     */
    public CountryState getStateByCode(String stateCode) {
        if (states != null) {
            return states.stream()
                .filter(state -> stateCode.equals(state.getStateCode()))
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    /**
     * Get a state by its name
     * 
     * @param stateName The state name to search for
     * @return The state if found, null otherwise
     */
    public CountryState getStateByName(String stateName) {
        if (states != null) {
            return states.stream()
                .filter(state -> stateName.equals(state.getStateName()))
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("Country{id=%d, countryName='%s', countryCode='%s'}", 
            id, countryName, countryCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Country country = (Country) obj;
        return id != null ? id.equals(country.id) : country.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (countryName != null ? countryName.hashCode() : 0);
        result = prime * result + (countryCode != null ? countryCode.hashCode() : 0);
        result = prime * result + (phoneCode != null ? phoneCode.hashCode() : 0);
        return result;
    }

    /**
     * Convert this Country object to JSON string
     * 
     * @return JSON representation of the Country
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Country to JSON", e);
        }
    }

    /**
     * Create a Country object from JSON string
     * 
     * @param json JSON string representation of Country
     * @return Country object
     */
    public static Country fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, Country.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to Country", e);
        }
    }

    /**
     * Convert this Country object to XML string
     * 
     * @return XML representation of the Country
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Country.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting Country to XML", e);
        }
    }

    /**
     * Create a Country object from XML string
     * 
     * @param xml XML string representation of Country
     * @return Country object
     */
    public static Country fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Country.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (Country) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to Country", e);
        }
    }
} 