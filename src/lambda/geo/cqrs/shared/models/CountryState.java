package com.mindthekid.geo.cqrs.shared.models;

import java.util.List;

/**
 * CountryState entity for state/province data within countries in the MTK Backend system.
 * 
 * This entity represents states, provinces, or similar administrative divisions
 * within a country. It maintains relationships with the parent country and
 * addresses within the state.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class CountryState {
    private Integer id;
    private Country country;
    private String stateName;
    private String stateCode;
    
    // Relationships
    private List<Address> addresses;

    /**
     * Default constructor
     */
    public CountryState() {}

    /**
     * Constructor with required fields
     * 
     * @param country Parent country
     * @param stateName Name of the state/province
     * @param stateCode Unique state code
     */
    public CountryState(Country country, String stateName, String stateCode) {
        this.country = country;
        this.stateName = stateName;
        this.stateCode = stateCode;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param country Parent country
     * @param stateName Name of the state/province
     * @param stateCode Unique state code
     */
    public CountryState(Integer id, Country country, String stateName, String stateCode) {
        this.id = id;
        this.country = country;
        this.stateName = stateName;
        this.stateCode = stateCode;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }

    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }

    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }

    /**
     * Check if this state has addresses
     * 
     * @return true if state has addresses, false otherwise
     */
    public boolean hasAddresses() {
        return addresses != null && !addresses.isEmpty();
    }

    /**
     * Get the number of addresses in this state
     * 
     * @return Number of addresses, 0 if no addresses
     */
    public int getAddressCount() {
        return addresses != null ? addresses.size() : 0;
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
     * Get the full location string (State, Country)
     * 
     * @return Formatted location string
     */
    public String getFullLocation() {
        if (country != null) {
            return stateName + ", " + country.getCountryName();
        }
        return stateName;
    }

    /**
     * Get the location code (StateCode-CountryCode)
     * 
     * @return Formatted location code
     */
    public String getLocationCode() {
        if (country != null) {
            return stateCode + "-" + country.getCountryCode();
        }
        return stateCode;
    }

    @Override
    public String toString() {
        return String.format("CountryState{id=%d, stateName='%s', stateCode='%s', country='%s'}", 
            id, stateName, stateCode, country != null ? country.getCountryName() : "null");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CountryState that = (CountryState) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (stateName != null ? stateName.hashCode() : 0);
        result = prime * result + (stateCode != null ? stateCode.hashCode() : 0);
        result = prime * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    /**
     * Convert this CountryState object to JSON string
     * 
     * @return JSON representation of the CountryState
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting CountryState to JSON", e);
        }
    }

    /**
     * Create a CountryState object from JSON string
     * 
     * @param json JSON string representation of CountryState
     * @return CountryState object
     */
    public static CountryState fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, CountryState.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to CountryState", e);
        }
    }

    /**
     * Convert this CountryState object to XML string
     * 
     * @return XML representation of the CountryState
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(CountryState.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting CountryState to XML", e);
        }
    }

    /**
     * Create a CountryState object from XML string
     * 
     * @param xml XML string representation of CountryState
     * @return CountryState object
     */
    public static CountryState fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(CountryState.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (CountryState) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to CountryState", e);
        }
    }
} 