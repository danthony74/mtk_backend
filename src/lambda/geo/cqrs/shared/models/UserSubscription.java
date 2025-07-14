package com.mindthekid.geo.cqrs.shared.models;

import java.time.LocalDate;
import java.util.Optional;

/**
 * UserSubscription entity representing user subscription relationships in the MTK Backend system.
 * 
 * This junction entity connects users with their subscriptions and includes additional
 * metadata such as start date and optional end date for tracking when users
 * subscribe or unsubscribe from services.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class UserSubscription {
    private Integer id;
    private User user;
    private SubscriptionType subscriptionType;
    private LocalDate startDate;
    private Optional<LocalDate> endDate;

    /**
     * Default constructor
     */
    public UserSubscription() {}

    /**
     * Constructor with required fields
     * 
     * @param user Associated user
     * @param subscriptionType Associated subscription type
     * @param startDate When subscription started
     */
    public UserSubscription(User user, SubscriptionType subscriptionType, LocalDate startDate) {
        this.user = user;
        this.subscriptionType = subscriptionType;
        this.startDate = startDate;
    }

    /**
     * Constructor with all fields
     * 
     * @param id Unique identifier
     * @param user Associated user
     * @param subscriptionType Associated subscription type
     * @param startDate When subscription started
     * @param endDate When subscription ended (optional)
     */
    public UserSubscription(Integer id, User user, SubscriptionType subscriptionType, 
                          LocalDate startDate, Optional<LocalDate> endDate) {
        this.id = id;
        this.user = user;
        this.subscriptionType = subscriptionType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SubscriptionType getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(SubscriptionType subscriptionType) { this.subscriptionType = subscriptionType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public Optional<LocalDate> getEndDate() { return endDate; }
    public void setEndDate(Optional<LocalDate> endDate) { this.endDate = endDate; }

    /**
     * Check if this user subscription is currently active
     * 
     * @return true if the subscription is active, false otherwise
     */
    public boolean isActive() {
        return endDate == null || !endDate.isPresent();
    }

    /**
     * Check if this user subscription is active on a specific date
     * 
     * @param date The date to check
     * @return true if the subscription is active on the specified date, false otherwise
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
     * Get the duration of this subscription in days
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
     * Get the user's display name
     * 
     * @return User's display name, null if user is not set
     */
    public String getUserName() {
        return user != null ? user.getDisplayName() : null;
    }

    /**
     * Get the subscription type ID
     * 
     * @return Subscription type ID, null if subscription type is not set
     */
    public Integer getSubscriptionTypeId() {
        return subscriptionType != null ? subscriptionType.getId() : null;
    }

    /**
     * Get the subscription type code
     * 
     * @return Subscription type code, null if subscription type is not set
     */
    public String getSubscriptionTypeCode() {
        return subscriptionType != null ? subscriptionType.getSubscriptionCode() : null;
    }

    /**
     * Get the subscription type description
     * 
     * @return Subscription type description, null if subscription type is not set
     */
    public String getSubscriptionTypeDesc() {
        return subscriptionType != null ? subscriptionType.getSubscriptionDesc() : null;
    }

    /**
     * Get the subscription cost
     * 
     * @return Subscription cost, null if subscription type is not set
     */
    public java.math.BigDecimal getCost() {
        return subscriptionType != null ? subscriptionType.getCost() : null;
    }

    /**
     * Get the subscription frequency
     * 
     * @return Subscription frequency, null if subscription type is not set
     */
    public Frequency getFrequency() {
        return subscriptionType != null ? subscriptionType.getFrequency() : null;
    }

    /**
     * Get the frequency code
     * 
     * @return Frequency code, null if frequency is not set
     */
    public String getFrequencyCode() {
        return subscriptionType != null && subscriptionType.getFrequency() != null ? 
               subscriptionType.getFrequency().getFrequencyCode() : null;
    }

    /**
     * Check if this is a premium subscription
     * 
     * @return true if this is a premium subscription, false otherwise
     */
    public boolean isPremium() {
        return subscriptionType != null && subscriptionType.isPremium();
    }

    /**
     * Check if this is a basic subscription
     * 
     * @return true if this is a basic subscription, false otherwise
     */
    public boolean isBasic() {
        return subscriptionType != null && subscriptionType.isBasic();
    }

    /**
     * Check if this is a free subscription
     * 
     * @return true if this is a free subscription, false otherwise
     */
    public boolean isFree() {
        return subscriptionType != null && subscriptionType.isFree();
    }

    /**
     * Check if this is a paid subscription
     * 
     * @return true if this is a paid subscription, false otherwise
     */
    public boolean isPaid() {
        return subscriptionType != null && subscriptionType.isPaid();
    }

    /**
     * Get the annual cost for this subscription
     * 
     * @return Annual cost, null if subscription type is not set
     */
    public java.math.BigDecimal getAnnualCost() {
        return subscriptionType != null ? subscriptionType.getAnnualCost() : null;
    }

    /**
     * Get the monthly equivalent cost for this subscription
     * 
     * @return Monthly equivalent cost, null if subscription type is not set
     */
    public java.math.BigDecimal getMonthlyEquivalentCost() {
        return subscriptionType != null ? subscriptionType.getMonthlyEquivalentCost() : null;
    }

    /**
     * Get the formatted cost string
     * 
     * @return Formatted cost string, null if subscription type is not set
     */
    public String getFormattedCost() {
        return subscriptionType != null ? subscriptionType.getFormattedCost() : null;
    }

    /**
     * Get the formatted annual cost string
     * 
     * @return Formatted annual cost string, null if subscription type is not set
     */
    public String getFormattedAnnualCost() {
        return subscriptionType != null ? subscriptionType.getFormattedAnnualCost() : null;
    }

    /**
     * End this subscription
     * 
     * @param endDate The date when the subscription ended
     */
    public void endSubscription(LocalDate endDate) {
        this.endDate = Optional.of(endDate);
    }

    /**
     * Reactivate this subscription
     */
    public void reactivate() {
        this.endDate = Optional.empty();
    }

    /**
     * Get the next billing date based on frequency
     * 
     * @param fromDate The date to calculate from (defaults to today if null)
     * @return Next billing date, or null if frequency is not set
     */
    public LocalDate getNextBillingDate(LocalDate fromDate) {
        if (subscriptionType == null || subscriptionType.getFrequency() == null) {
            return null;
        }
        
        LocalDate baseDate = fromDate != null ? fromDate : LocalDate.now();
        Frequency frequency = subscriptionType.getFrequency();
        
        if (frequency.isDaily()) {
            return baseDate.plusDays(1);
        } else if (frequency.isWeekly()) {
            return baseDate.plusWeeks(1);
        } else if (frequency.isMonthly()) {
            return baseDate.plusMonths(1);
        } else if (frequency.isQuarterly()) {
            return baseDate.plusMonths(3);
        } else if (frequency.isYearly()) {
            return baseDate.plusYears(1);
        } else if (frequency.isOneTime()) {
            return null; // One-time subscriptions don't have next billing
        }
        
        return null; // Unknown frequency
    }

    @Override
    public String toString() {
        return String.format("UserSubscription{id=%d, user='%s', subscriptionType='%s', startDate=%s, active=%s}", 
            id, 
            user != null ? user.getId() : "null",
            subscriptionType != null ? subscriptionType.getSubscriptionCode() : "null",
            startDate,
            isActive());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UserSubscription that = (UserSubscription) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id != null ? id.hashCode() : 0);
        result = prime * result + (user != null ? user.hashCode() : 0);
        result = prime * result + (subscriptionType != null ? subscriptionType.hashCode() : 0);
        result = prime * result + (startDate != null ? startDate.hashCode() : 0);
        result = prime * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    /**
     * Convert this UserSubscription object to JSON string
     * 
     * @return JSON representation of the UserSubscription
     */
    public String toJson() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting UserSubscription to JSON", e);
        }
    }

    /**
     * Create a UserSubscription object from JSON string
     * 
     * @param json JSON string representation of UserSubscription
     * @return UserSubscription object
     */
    public static UserSubscription fromJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            return mapper.readValue(json, UserSubscription.class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to UserSubscription", e);
        }
    }

    /**
     * Convert this UserSubscription object to XML string
     * 
     * @return XML representation of the UserSubscription
     */
    public String toXml() {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(UserSubscription.class);
            javax.xml.bind.Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error converting UserSubscription to XML", e);
        }
    }

    /**
     * Create a UserSubscription object from XML string
     * 
     * @param xml XML string representation of UserSubscription
     * @return UserSubscription object
     */
    public static UserSubscription fromXml(String xml) {
        try {
            javax.xml.bind.JAXBContext context = javax.xml.bind.JAXBContext.newInstance(UserSubscription.class);
            javax.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
            
            java.io.StringReader reader = new java.io.StringReader(xml);
            return (UserSubscription) unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            throw new RuntimeException("Error converting XML to UserSubscription", e);
        }
    }
} 