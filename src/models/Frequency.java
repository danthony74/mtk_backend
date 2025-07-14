package com.mindthekid.models;

import java.util.List;

/**
 * Frequency entity for subscription billing frequencies in the MTK Backend system.
 * 
 * This entity provides reference data for categorizing subscription billing
 * frequencies (e.g., monthly, quarterly, yearly). It maintains relationships
 * with subscription types that use this frequency.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class Frequency {
    private Integer id;
    private String frequencyDesc;
    private String frequencyCode;
    
    // Relationships
    private List<SubscriptionType> subscriptionTypes;

    /**
     * Default constructor
     */
    public Frequency() {}

    /**
     * Constructor with required fields
     * 
     * @param frequencyDesc Human-readable frequency description
     * @param frequencyCode Unique frequency code
     */
    public Frequency(String frequencyDesc, String frequencyCode) {
        this.frequencyDesc = frequencyDesc;
        this.frequencyCode = frequencyCode;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param frequencyDesc Human-readable frequency description
     * @param frequencyCode Unique frequency code
     */
    public Frequency(Integer id, String frequencyDesc, String frequencyCode) {
        this.id = id;
        this.frequencyDesc = frequencyDesc;
        this.frequencyCode = frequencyCode;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFrequencyDesc() { return frequencyDesc; }
    public void setFrequencyDesc(String frequencyDesc) { this.frequencyDesc = frequencyDesc; }

    public String getFrequencyCode() { return frequencyCode; }
    public void setFrequencyCode(String frequencyCode) { this.frequencyCode = frequencyCode; }

    public List<SubscriptionType> getSubscriptionTypes() { return subscriptionTypes; }
    public void setSubscriptionTypes(List<SubscriptionType> subscriptionTypes) { this.subscriptionTypes = subscriptionTypes; }

    /**
     * Check if this frequency has associated subscription types
     * 
     * @return true if frequency has subscription types, false otherwise
     */
    public boolean hasSubscriptionTypes() {
        return subscriptionTypes != null && !subscriptionTypes.isEmpty();
    }

    /**
     * Get the number of subscription types using this frequency
     * 
     * @return Number of subscription types, 0 if none
     */
    public int getSubscriptionTypeCount() {
        return subscriptionTypes != null ? subscriptionTypes.size() : 0;
    }

    /**
     * Check if this is a monthly frequency
     * 
     * @return true if this is a monthly frequency, false otherwise
     */
    public boolean isMonthly() {
        return "MONTHLY".equalsIgnoreCase(frequencyCode) || 
               "MONTH".equalsIgnoreCase(frequencyCode);
    }

    /**
     * Check if this is a quarterly frequency
     * 
     * @return true if this is a quarterly frequency, false otherwise
     */
    public boolean isQuarterly() {
        return "QUARTERLY".equalsIgnoreCase(frequencyCode) || 
               "QUARTER".equalsIgnoreCase(frequencyCode);
    }

    /**
     * Check if this is a yearly frequency
     * 
     * @return true if this is a yearly frequency, false otherwise
     */
    public boolean isYearly() {
        return "YEARLY".equalsIgnoreCase(frequencyCode) || 
               "YEAR".equalsIgnoreCase(frequencyCode) ||
               "ANNUAL".equalsIgnoreCase(frequencyCode);
    }

    /**
     * Check if this is a weekly frequency
     * 
     * @return true if this is a weekly frequency, false otherwise
     */
    public boolean isWeekly() {
        return "WEEKLY".equalsIgnoreCase(frequencyCode) || 
               "WEEK".equalsIgnoreCase(frequencyCode);
    }

    /**
     * Check if this is a daily frequency
     * 
     * @return true if this is a daily frequency, false otherwise
     */
    public boolean isDaily() {
        return "DAILY".equalsIgnoreCase(frequencyCode) || 
               "DAY".equalsIgnoreCase(frequencyCode);
    }

    /**
     * Check if this is a one-time frequency
     * 
     * @return true if this is a one-time frequency, false otherwise
     */
    public boolean isOneTime() {
        return "ONE_TIME".equalsIgnoreCase(frequencyCode) || 
               "SINGLE".equalsIgnoreCase(frequencyCode) ||
               "ONCE".equalsIgnoreCase(frequencyCode);
    }

    /**
     * Get the number of days in this frequency period
     * 
     * @return Number of days, or null if frequency is not recognized
     */
    public Integer getDaysInPeriod() {
        if (isDaily()) return 1;
        if (isWeekly()) return 7;
        if (isMonthly()) return 30;
        if (isQuarterly()) return 90;
        if (isYearly()) return 365;
        if (isOneTime()) return null; // One-time has no recurring period
        return null; // Unknown frequency
    }

    /**
     * Get the number of months in this frequency period
     * 
     * @return Number of months, or null if frequency is not recognized
     */
    public Integer getMonthsInPeriod() {
        if (isDaily()) return 0;
        if (isWeekly()) return 0;
        if (isMonthly()) return 1;
        if (isQuarterly()) return 3;
        if (isYearly()) return 12;
        if (isOneTime()) return null; // One-time has no recurring period
        return null; // Unknown frequency
    }

    /**
     * Get the number of years in this frequency period
     * 
     * @return Number of years, or null if frequency is not recognized
     */
    public Integer getYearsInPeriod() {
        if (isDaily()) return 0;
        if (isWeekly()) return 0;
        if (isMonthly()) return 0;
        if (isQuarterly()) return 0;
        if (isYearly()) return 1;
        if (isOneTime()) return null; // One-time has no recurring period
        return null; // Unknown frequency
    }

    /**
     * Check if this frequency is recurring
     * 
     * @return true if this is a recurring frequency, false otherwise
     */
    public boolean isRecurring() {
        return !isOneTime();
    }

    @Override
    public String toString() {
        return String.format("Frequency{id=%d, frequencyDesc='%s', frequencyCode='%s'}", 
            id, frequencyDesc, frequencyCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Frequency that = (Frequency) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (frequencyDesc != null ? frequencyDesc.hashCode() : 0);
        result = prime * result + (frequencyCode != null ? frequencyCode.hashCode() : 0);
        return result;
    }

    /**
     * Convert this Frequency object to JSON string
     * 
     * @return JSON representation of the Frequency
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Frequency to JSON", e);
        }
    }

    /**
     * Create a Frequency object from JSON string
     * 
     * @param json JSON string representation of Frequency
     * @return Frequency object
     */
    public static Frequency fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, Frequency.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to Frequency", e);
        }
    }

    /**
     * Convert this Frequency object to XML string
     * 
     * @return XML representation of the Frequency
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Frequency.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting Frequency to XML", e);
        }
    }

    /**
     * Create a Frequency object from XML string
     * 
     * @param xml XML string representation of Frequency
     * @return Frequency object
     */
    public static Frequency fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(Frequency.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (Frequency) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to Frequency", e);
        }
    }
} 