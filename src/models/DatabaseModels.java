package com.mindthekid.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Database Model Classes for MTK Backend System
 * 
 * These classes represent the database schema with proper relationships
 * and follow Java best practices for data modeling.
 * 
 * @author MTK Backend Team
 * @version 1.0.0
 */
public class DatabaseModels {

    /**
     * User entity representing the core user in the system
     */
    public static class User {
        private String id;
        private String email;
        private String firstName;
        private Optional<String> middleNameInitial;
        private String familyName;
        private String phone;
        private Gender gender;
        private String userHash;
        
        // Relationships
        private List<UserAddress> addresses;
        private List<FamilyMembership> familyMemberships;
        private List<UserSubscription> subscriptions;
        private List<Family> createdFamilies;
        private List<UserShare> sharedBy;
        private List<UserShare> sharedWith;

        // Constructors
        public User() {}

        public User(String id, String email, String firstName, String familyName, 
                   String phone, Gender gender, String userHash) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.familyName = familyName;
            this.phone = phone;
            this.gender = gender;
            this.userHash = userHash;
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public Optional<String> getMiddleNameInitial() { return middleNameInitial; }
        public void setMiddleNameInitial(Optional<String> middleNameInitial) { this.middleNameInitial = middleNameInitial; }

        public String getFamilyName() { return familyName; }
        public void setFamilyName(String familyName) { this.familyName = familyName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public Gender getGender() { return gender; }
        public void setGender(Gender gender) { this.gender = gender; }

        public String getUserHash() { return userHash; }
        public void setUserHash(String userHash) { this.userHash = userHash; }

        // Relationship getters and setters
        public List<UserAddress> getAddresses() { return addresses; }
        public void setAddresses(List<UserAddress> addresses) { this.addresses = addresses; }

        public List<FamilyMembership> getFamilyMemberships() { return familyMemberships; }
        public void setFamilyMemberships(List<FamilyMembership> familyMemberships) { this.familyMemberships = familyMemberships; }

        public List<UserSubscription> getSubscriptions() { return subscriptions; }
        public void setSubscriptions(List<UserSubscription> subscriptions) { this.subscriptions = subscriptions; }

        public List<Family> getCreatedFamilies() { return createdFamilies; }
        public void setCreatedFamilies(List<Family> createdFamilies) { this.createdFamilies = createdFamilies; }

        public List<UserShare> getSharedBy() { return sharedBy; }
        public void setSharedBy(List<UserShare> sharedBy) { this.sharedBy = sharedBy; }

        public List<UserShare> getSharedWith() { return sharedWith; }
        public void setSharedWith(List<UserShare> sharedWith) { this.sharedWith = sharedWith; }

        @Override
        public String toString() {
            return String.format("User{id='%s', email='%s', firstName='%s', familyName='%s'}", 
                id, email, firstName, familyName);
        }
    }

    /**
     * Gender entity for user gender classification
     */
    public static class Gender {
        private Integer id;
        private String genderDesc;
        private String genderCode;
        
        // Relationships
        private List<User> users;

        public Gender() {}

        public Gender(String genderDesc, String genderCode) {
            this.genderDesc = genderDesc;
            this.genderCode = genderCode;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getGenderDesc() { return genderDesc; }
        public void setGenderDesc(String genderDesc) { this.genderDesc = genderDesc; }

        public String getGenderCode() { return genderCode; }
        public void setGenderCode(String genderCode) { this.genderCode = genderCode; }

        public List<User> getUsers() { return users; }
        public void setUsers(List<User> users) { this.users = users; }

        @Override
        public String toString() {
            return String.format("Gender{id=%d, genderDesc='%s', genderCode='%s'}", 
                id, genderDesc, genderCode);
        }
    }

    /**
     * Country entity for geographical data
     */
    public static class Country {
        private Integer id;
        private String countryName;
        private String encoding;
        private String phonePrefix;
        private String countryCode;
        
        // Relationships
        private List<CountryState> states;
        private List<Address> addresses;

        public Country() {}

        public Country(String countryName, String encoding, String phonePrefix, String countryCode) {
            this.countryName = countryName;
            this.encoding = encoding;
            this.phonePrefix = phonePrefix;
            this.countryCode = countryCode;
        }

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

        @Override
        public String toString() {
            return String.format("Country{id=%d, countryName='%s', countryCode='%s'}", 
                id, countryName, countryCode);
        }
    }

    /**
     * CountryState entity for state/province data within countries
     */
    public static class CountryState {
        private Integer id;
        private Country country;
        private String stateName;
        private String stateCode;
        
        // Relationships
        private List<Address> addresses;

        public CountryState() {}

        public CountryState(Country country, String stateName, String stateCode) {
            this.country = country;
            this.stateName = stateName;
            this.stateCode = stateCode;
        }

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

        @Override
        public String toString() {
            return String.format("CountryState{id=%d, stateName='%s', stateCode='%s'}", 
                id, stateName, stateCode);
        }
    }

    /**
     * Address entity for physical address data
     */
    public static class Address {
        private Integer id;
        private String addressLineOne;
        private String addressLineTwo;
        private Optional<String> addressLineThree;
        private Country country;
        private Optional<CountryState> state;
        
        // Relationships
        private List<UserAddress> userAddresses;

        public Address() {}

        public Address(String addressLineOne, String addressLineTwo, Country country) {
            this.addressLineOne = addressLineOne;
            this.addressLineTwo = addressLineTwo;
            this.country = country;
        }

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

        @Override
        public String toString() {
            return String.format("Address{id=%d, addressLineOne='%s', country='%s'}", 
                id, addressLineOne, country != null ? country.getCountryName() : "null");
        }
    }

    /**
     * AddressType entity for categorizing address types
     */
    public static class AddressType {
        private Integer id;
        private String addressTypeDesc;
        private String addressTypeCode;
        
        // Relationships
        private List<UserAddress> userAddresses;

        public AddressType() {}

        public AddressType(String addressTypeDesc, String addressTypeCode) {
            this.addressTypeDesc = addressTypeDesc;
            this.addressTypeCode = addressTypeCode;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getAddressTypeDesc() { return addressTypeDesc; }
        public void setAddressTypeDesc(String addressTypeDesc) { this.addressTypeDesc = addressTypeDesc; }

        public String getAddressTypeCode() { return addressTypeCode; }
        public void setAddressTypeCode(String addressTypeCode) { this.addressTypeCode = addressTypeCode; }

        public List<UserAddress> getUserAddresses() { return userAddresses; }
        public void setUserAddresses(List<UserAddress> userAddresses) { this.userAddresses = userAddresses; }

        @Override
        public String toString() {
            return String.format("AddressType{id=%d, addressTypeDesc='%s', addressTypeCode='%s'}", 
                id, addressTypeDesc, addressTypeCode);
        }
    }

    /**
     * UserAddress entity representing the many-to-many relationship between users and addresses
     */
    public static class UserAddress {
        private User user;
        private Address address;
        private AddressType addressType;
        private LocalDate startDate;
        private Optional<LocalDate> endDate;

        public UserAddress() {}

        public UserAddress(User user, Address address, AddressType addressType, LocalDate startDate) {
            this.user = user;
            this.address = address;
            this.addressType = addressType;
            this.startDate = startDate;
        }

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

        @Override
        public String toString() {
            return String.format("UserAddress{user='%s', address='%s', addressType='%s', startDate=%s}", 
                user != null ? user.getId() : "null", 
                address != null ? address.getId() : "null",
                addressType != null ? addressType.getAddressTypeCode() : "null",
                startDate);
        }
    }

    /**
     * Family entity representing a family group
     */
    public static class Family {
        private Integer id;
        private User creator;
        private String familyName;
        private LocalDate dateStarted;
        private Optional<LocalDate> dateEnded;
        
        // Relationships
        private List<FamilyMembership> memberships;

        public Family() {}

        public Family(User creator, String familyName, LocalDate dateStarted) {
            this.creator = creator;
            this.familyName = familyName;
            this.dateStarted = dateStarted;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public User getCreator() { return creator; }
        public void setCreator(User creator) { this.creator = creator; }

        public String getFamilyName() { return familyName; }
        public void setFamilyName(String familyName) { this.familyName = familyName; }

        public LocalDate getDateStarted() { return dateStarted; }
        public void setDateStarted(LocalDate dateStarted) { this.dateStarted = dateStarted; }

        public Optional<LocalDate> getDateEnded() { return dateEnded; }
        public void setDateEnded(Optional<LocalDate> dateEnded) { this.dateEnded = dateEnded; }

        public List<FamilyMembership> getMemberships() { return memberships; }
        public void setMemberships(List<FamilyMembership> memberships) { this.memberships = memberships; }

        @Override
        public String toString() {
            return String.format("Family{id=%d, familyName='%s', creator='%s'}", 
                id, familyName, creator != null ? creator.getId() : "null");
        }
    }

    /**
     * FamilyMemberType entity for categorizing family member roles
     */
    public static class FamilyMemberType {
        private Integer id;
        private String memberTypeDesc;
        private String memberTypeCode;
        
        // Relationships
        private List<FamilyMembership> memberships;

        public FamilyMemberType() {}

        public FamilyMemberType(String memberTypeDesc, String memberTypeCode) {
            this.memberTypeDesc = memberTypeDesc;
            this.memberTypeCode = memberTypeCode;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getMemberTypeDesc() { return memberTypeDesc; }
        public void setMemberTypeDesc(String memberTypeDesc) { this.memberTypeDesc = memberTypeDesc; }

        public String getMemberTypeCode() { return memberTypeCode; }
        public void setMemberTypeCode(String memberTypeCode) { this.memberTypeCode = memberTypeCode; }

        public List<FamilyMembership> getMemberships() { return memberships; }
        public void setMemberships(List<FamilyMembership> memberships) { this.memberships = memberships; }

        @Override
        public String toString() {
            return String.format("FamilyMemberType{id=%d, memberTypeDesc='%s', memberTypeCode='%s'}", 
                id, memberTypeDesc, memberTypeCode);
        }
    }

    /**
     * FamilyMembership entity representing the many-to-many relationship between users and families
     */
    public static class FamilyMembership {
        private Integer id;
        private Family family;
        private User user;
        private FamilyMemberType membershipType;
        private LocalDateTime whenJoined;
        private Optional<LocalDateTime> whenLeft;

        public FamilyMembership() {}

        public FamilyMembership(Family family, User user, FamilyMemberType membershipType, LocalDateTime whenJoined) {
            this.family = family;
            this.user = user;
            this.membershipType = membershipType;
            this.whenJoined = whenJoined;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public Family getFamily() { return family; }
        public void setFamily(Family family) { this.family = family; }

        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }

        public FamilyMemberType getMembershipType() { return membershipType; }
        public void setMembershipType(FamilyMemberType membershipType) { this.membershipType = membershipType; }

        public LocalDateTime getWhenJoined() { return whenJoined; }
        public void setWhenJoined(LocalDateTime whenJoined) { this.whenJoined = whenJoined; }

        public Optional<LocalDateTime> getWhenLeft() { return whenLeft; }
        public void setWhenLeft(Optional<LocalDateTime> whenLeft) { this.whenLeft = whenLeft; }

        @Override
        public String toString() {
            return String.format("FamilyMembership{id=%d, family='%s', user='%s', membershipType='%s'}", 
                id, 
                family != null ? family.getFamilyName() : "null",
                user != null ? user.getId() : "null",
                membershipType != null ? membershipType.getMemberTypeCode() : "null");
        }
    }

    /**
     * Frequency entity for subscription billing frequencies
     */
    public static class Frequency {
        private Integer id;
        private String frequencyDesc;
        private String frequencyCode;
        
        // Relationships
        private List<SubscriptionType> subscriptionTypes;

        public Frequency() {}

        public Frequency(String frequencyDesc, String frequencyCode) {
            this.frequencyDesc = frequencyDesc;
            this.frequencyCode = frequencyCode;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getFrequencyDesc() { return frequencyDesc; }
        public void setFrequencyDesc(String frequencyDesc) { this.frequencyDesc = frequencyDesc; }

        public String getFrequencyCode() { return frequencyCode; }
        public void setFrequencyCode(String frequencyCode) { this.frequencyCode = frequencyCode; }

        public List<SubscriptionType> getSubscriptionTypes() { return subscriptionTypes; }
        public void setSubscriptionTypes(List<SubscriptionType> subscriptionTypes) { this.subscriptionTypes = subscriptionTypes; }

        @Override
        public String toString() {
            return String.format("Frequency{id=%d, frequencyDesc='%s', frequencyCode='%s'}", 
                id, frequencyDesc, frequencyCode);
        }
    }

    /**
     * SubscriptionType entity for different subscription plans
     */
    public static class SubscriptionType {
        private Integer id;
        private String subscriptionDesc;
        private String subscriptionCode;
        private BigDecimal cost;
        private Frequency frequency;
        private LocalDate availableStartDate;
        private Optional<LocalDate> availableEndDate;
        
        // Relationships
        private List<UserSubscription> userSubscriptions;

        public SubscriptionType() {}

        public SubscriptionType(String subscriptionDesc, String subscriptionCode, 
                              BigDecimal cost, Frequency frequency, LocalDate availableStartDate) {
            this.subscriptionDesc = subscriptionDesc;
            this.subscriptionCode = subscriptionCode;
            this.cost = cost;
            this.frequency = frequency;
            this.availableStartDate = availableStartDate;
        }

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

        @Override
        public String toString() {
            return String.format("SubscriptionType{id=%d, subscriptionDesc='%s', subscriptionCode='%s', cost=%s}", 
                id, subscriptionDesc, subscriptionCode, cost);
        }
    }

    /**
     * UserSubscription entity representing user subscription relationships
     */
    public static class UserSubscription {
        private Integer id;
        private User user;
        private SubscriptionType subscriptionType;
        private LocalDate startDate;
        private Optional<LocalDate> endDate;

        public UserSubscription() {}

        public UserSubscription(User user, SubscriptionType subscriptionType, LocalDate startDate) {
            this.user = user;
            this.subscriptionType = subscriptionType;
            this.startDate = startDate;
        }

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

        @Override
        public String toString() {
            return String.format("UserSubscription{id=%d, user='%s', subscriptionType='%s', startDate=%s}", 
                id, 
                user != null ? user.getId() : "null",
                subscriptionType != null ? subscriptionType.getSubscriptionCode() : "null",
                startDate);
        }
    }

    /**
     * ShareType entity for categorizing sharing relationships
     */
    public static class ShareType {
        private Integer id;
        private String shareTypeDesc;
        private String shareTypeCode;
        
        // Relationships
        private List<UserShare> userShares;

        public ShareType() {}

        public ShareType(String shareTypeDesc, String shareTypeCode) {
            this.shareTypeDesc = shareTypeDesc;
            this.shareTypeCode = shareTypeCode;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getShareTypeDesc() { return shareTypeDesc; }
        public void setShareTypeDesc(String shareTypeDesc) { this.shareTypeDesc = shareTypeDesc; }

        public String getShareTypeCode() { return shareTypeCode; }
        public void setShareTypeCode(String shareTypeCode) { this.shareTypeCode = shareTypeCode; }

        public List<UserShare> getUserShares() { return userShares; }
        public void setUserShares(List<UserShare> userShares) { this.userShares = userShares; }

        @Override
        public String toString() {
            return String.format("ShareType{id=%d, shareTypeDesc='%s', shareTypeCode='%s'}", 
                id, shareTypeDesc, shareTypeCode);
        }
    }

    /**
     * UserShare entity representing sharing relationships between users
     */
    public static class UserShare {
        private User sharedBy;
        private User sharedWith;
        private ShareType shareType;
        private LocalDateTime whenShared;

        public UserShare() {}

        public UserShare(User sharedBy, User sharedWith, ShareType shareType, LocalDateTime whenShared) {
            this.sharedBy = sharedBy;
            this.sharedWith = sharedWith;
            this.shareType = shareType;
            this.whenShared = whenShared;
        }

        public User getSharedBy() { return sharedBy; }
        public void setSharedBy(User sharedBy) { this.sharedBy = sharedBy; }

        public User getSharedWith() { return sharedWith; }
        public void setSharedWith(User sharedWith) { this.sharedWith = sharedWith; }

        public ShareType getShareType() { return shareType; }
        public void setShareType(ShareType shareType) { this.shareType = shareType; }

        public LocalDateTime getWhenShared() { return whenShared; }
        public void setWhenShared(LocalDateTime whenShared) { this.whenShared = whenShared; }

        @Override
        public String toString() {
            return String.format("UserShare{sharedBy='%s', sharedWith='%s', shareType='%s', whenShared=%s}", 
                sharedBy != null ? sharedBy.getId() : "null",
                sharedWith != null ? sharedWith.getId() : "null",
                shareType != null ? shareType.getShareTypeCode() : "null",
                whenShared);
        }
    }

    /**
     * VersionInfo entity for tracking component versions
     */
    public static class VersionInfo {
        private String component;
        private BigDecimal versionNumber;

        public VersionInfo() {}

        public VersionInfo(String component, BigDecimal versionNumber) {
            this.component = component;
            this.versionNumber = versionNumber;
        }

        public String getComponent() { return component; }
        public void setComponent(String component) { this.component = component; }

        public BigDecimal getVersionNumber() { return versionNumber; }
        public void setVersionNumber(BigDecimal versionNumber) { this.versionNumber = versionNumber; }

        @Override
        public String toString() {
            return String.format("VersionInfo{component='%s', versionNumber=%s}", 
                component, versionNumber);
        }
    }
} 