package com.mindthekid.geo.cqrs.shared.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * SubscriptionType entity for different subscription plans in the MTK Backend system.
 * 
 * This entity represents available subscription plans with pricing, billing frequency,
 * and availability periods. It maintains relationships with user subscriptions
 * that use this type.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class SubscriptionType {
    private Integer id;
    private String subscriptionDesc;
    private String subscriptionCode;
    private BigDecimal cost;
    private Frequency frequency;
    private LocalDate availableStartDate;
    private Optional<LocalDate> availableEndDate;
    
    // Relationships
    private List<UserSubscription> userSubscriptions;

    /**
     * Default constructor
     */
    public SubscriptionType() {}

    /**
     * Constructor with required fields
     * 
     * @param subscriptionDesc Description of the subscription
     * @param subscriptionCode Unique subscription code
     * @param cost Cost of the subscription
     * @param frequency Billing frequency
     * @param availableStartDate When subscription becomes available
     */
    public SubscriptionType(String subscriptionDesc, String subscriptionCode, 
                          BigDecimal cost, Frequency frequency, LocalDate availableStartDate) {
        this.subscriptionDesc = subscriptionDesc;
        this.subscriptionCode = subscriptionCode;
        this.cost = cost;
        this.frequency = frequency;
        this.availableStartDate = availableStartDate;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param subscriptionDesc Description of the subscription
     * @param subscriptionCode Unique subscription code
     * @param cost Cost of the subscription
     * @param frequency Billing frequency
     * @param availableStartDate When subscription becomes available
     * @param availableEndDate When subscription expires (optional)
     */
    public SubscriptionType(Integer id, String subscriptionDesc, String subscriptionCode, 
                          BigDecimal cost, Frequency frequency, LocalDate availableStartDate, 
                          Optional<LocalDate> availableEndDate) {
        this.id = id;
        this.subscriptionDesc = subscriptionDesc;
        this.subscriptionCode = subscriptionCode;
        this.cost = cost;
        this.frequency = frequency;
        this.availableStartDate = availableStartDate;
        this.availableEndDate = availableEndDate;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSubscriptionDesc() { return subscriptionDesc; }
    public void setSubscriptionDesc(String subscriptionDesc) { this.subscriptionDesc = subscriptionDesc; }

    public String getSubscriptionCode() { return subscriptionCode; }
    public void setSubscriptionCode(String subscriptionCode) { this.subscriptionCode = subscriptionCode; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }

    public LocalDate getAvailableStartDate() { return availableStartDate; }
    public void setAvailableStartDate(LocalDate availableStartDate) { this.availableStartDate = availableStartDate; }

    public Optional<LocalDate> getAvailableEndDate() { return availableEndDate; }
    public void setAvailableEndDate(Optional<LocalDate> availableEndDate) { this.availableEndDate = availableEndDate; }

    public List<UserSubscription> getUserSubscriptions() { return userSubscriptions; }
    public void setUserSubscriptions(List<UserSubscription> userSubscriptions) { this.userSubscriptions = userSubscriptions; }

    /**
     * Check if this subscription type is currently available
     * 
     * @return true if the subscription type is available, false otherwise
     */
    public boolean isAvailable() {
        LocalDate today = LocalDate.now();
        
        // Check if today is after or equal to start date
        if (today.isBefore(availableStartDate)) {
            return false;
        }
        
        // Check if today is before end date (if end date exists)
        if (availableEndDate != null && availableEndDate.isPresent() && !today.isBefore(availableEndDate.get())) {
            return false;
        }
        
        return true;
    }

    /**
     * Check if this subscription type is available on a specific date
     * 
     * @param date The date to check
     * @return true if the subscription type is available on the specified date, false otherwise
     */
    public boolean isAvailableOn(LocalDate date) {
        if (date == null) return false;
        
        // Check if date is after or equal to start date
        if (date.isBefore(availableStartDate)) {
            return false;
        }
        
        // Check if date is before end date (if end date exists)
        if (availableEndDate != null && availableEndDate.isPresent() && !date.isBefore(availableEndDate.get())) {
            return false;
        }
        
        return true;
    }

    /**
     * Check if this subscription type has associated user subscriptions
     * 
     * @return true if subscription type has user subscriptions, false otherwise
     */
    public boolean hasUserSubscriptions() {
        return userSubscriptions != null && !userSubscriptions.isEmpty();
    }

    /**
     * Get the number of user subscriptions using this type
     * 
     * @return Number of user subscriptions, 0 if none
     */
    public int getUserSubscriptionCount() {
        return userSubscriptions != null ? userSubscriptions.size() : 0;
    }

    /**
     * Get the frequency code
     * 
     * @return Frequency code, null if frequency is not set
     */
    public String getFrequencyCode() {
        return frequency != null ? frequency.getFrequencyCode() : null;
    }

    /**
     * Get the frequency description
     * 
     * @return Frequency description, null if frequency is not set
     */
    public String getFrequencyDesc() {
        return frequency != null ? frequency.getFrequencyDesc() : null;
    }

    /**
     * Get the annual cost (cost * frequency multiplier)
     * 
     * @return Annual cost, or null if frequency is not set
     */
    public BigDecimal getAnnualCost() {
        if (cost == null || frequency == null) return null;
        
        if (frequency.isYearly()) return cost;
        if (frequency.isQuarterly()) return cost.multiply(BigDecimal.valueOf(4));
        if (frequency.isMonthly()) return cost.multiply(BigDecimal.valueOf(12));
        if (frequency.isWeekly()) return cost.multiply(BigDecimal.valueOf(52));
        if (frequency.isDaily()) return cost.multiply(BigDecimal.valueOf(365));
        if (frequency.isOneTime()) return cost;
        
        return null; // Unknown frequency
    }

    /**
     * Get the monthly equivalent cost
     * 
     * @return Monthly equivalent cost, or null if frequency is not set
     */
    public BigDecimal getMonthlyEquivalentCost() {
        if (cost == null || frequency == null) return null;
        
        if (frequency.isMonthly()) return cost;
        if (frequency.isYearly()) return cost.divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP);
        if (frequency.isQuarterly()) return cost.divide(BigDecimal.valueOf(3), 2, BigDecimal.ROUND_HALF_UP);
        if (frequency.isWeekly()) return cost.multiply(BigDecimal.valueOf(4.33)); // Average weeks per month
        if (frequency.isDaily()) return cost.multiply(BigDecimal.valueOf(30.44)); // Average days per month
        if (frequency.isOneTime()) return cost; // One-time cost
        
        return null; // Unknown frequency
    }

    /**
     * Check if this is a premium subscription
     * 
     * @return true if this is a premium subscription, false otherwise
     */
    public boolean isPremium() {
        return "PREMIUM".equalsIgnoreCase(subscriptionCode) || 
               subscriptionDesc != null && subscriptionDesc.toLowerCase().contains("premium");
    }

    /**
     * Check if this is a basic subscription
     * 
     * @return true if this is a basic subscription, false otherwise
     */
    public boolean isBasic() {
        return "BASIC".equalsIgnoreCase(subscriptionCode) || 
               subscriptionDesc != null && subscriptionDesc.toLowerCase().contains("basic");
    }

    /**
     * Check if this is a free subscription
     * 
     * @return true if this is a free subscription, false otherwise
     */
    public boolean isFree() {
        return cost == null || cost.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Check if this is a paid subscription
     * 
     * @return true if this is a paid subscription, false otherwise
     */
    public boolean isPaid() {
        return !isFree();
    }

    /**
     * Get the formatted cost string
     * 
     * @return Formatted cost string with currency symbol
     */
    public String getFormattedCost() {
        if (cost == null) return "Free";
        return "$" + cost.toString();
    }

    /**
     * Get the formatted annual cost string
     * 
     * @return Formatted annual cost string with currency symbol
     */
    public String getFormattedAnnualCost() {
        BigDecimal annualCost = getAnnualCost();
        if (annualCost == null) return "N/A";
        return "$" + annualCost.toString();
    }

    @Override
    public String toString() {
        return String.format("SubscriptionType{id=%d, subscriptionDesc='%s', subscriptionCode='%s', cost=%s}", 
            id, subscriptionDesc, subscriptionCode, cost);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        SubscriptionType that = (SubscriptionType) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (subscriptionDesc != null ? subscriptionDesc.hashCode() : 0);
        result = prime * result + (subscriptionCode != null ? subscriptionCode.hashCode() : 0);
        result = prime * result + (cost != null ? cost.hashCode() : 0);
        result = prime * result + (frequency != null ? frequency.hashCode() : 0);
        result = prime * result + (availableStartDate != null ? availableStartDate.hashCode() : 0);
        result = prime * result + (availableEndDate != null ? availableEndDate.hashCode() : 0);
        return result;
    }

    /**
     * Convert this SubscriptionType object to JSON string
     * 
     * @return JSON representation of the SubscriptionType
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting SubscriptionType to JSON", e);
        }
    }

    /**
     * Create a SubscriptionType object from JSON string
     * 
     * @param json JSON string representation of SubscriptionType
     * @return SubscriptionType object
     */
    public static SubscriptionType fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.readValue(json, SubscriptionType.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to SubscriptionType", e);
        }
    }

    /**
     * Convert this SubscriptionType object to XML string
     * 
     * @return XML representation of the SubscriptionType
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(SubscriptionType.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting SubscriptionType to XML", e);
        }
    }

    /**
     * Create a SubscriptionType object from XML string
     * 
     * @param xml XML string representation of SubscriptionType
     * @return SubscriptionType object
     */
    public static SubscriptionType fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(SubscriptionType.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (SubscriptionType) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to SubscriptionType", e);
        }
    }
} 