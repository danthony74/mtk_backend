# Database Model Classes

## Overview

The database model classes represent the complete MTK Backend database schema with proper Java object relationships. These classes follow best practices for data modeling and provide a clean, type-safe interface for working with the database.

## Core Entities

### 1. User (Central Entity)
The `User` class is the central entity in the system, representing individual users with the following relationships:

**Direct Properties:**
- `id` (String) - Unique user identifier
- `email` (String) - User's email address
- `firstName` (String) - User's first name
- `middleNameInitial` (Optional<String>) - Optional middle initial
- `familyName` (String) - User's family name
- `phone` (String) - User's phone number
- `gender` (Gender) - User's gender classification
- `userHash` (String) - Hashed user credentials

**Relationships:**
- **One-to-Many**: `List<UserAddress>` - User can have multiple addresses
- **One-to-Many**: `List<FamilyMembership>` - User can belong to multiple families
- **One-to-Many**: `List<UserSubscription>` - User can have multiple subscriptions
- **One-to-Many**: `List<Family>` - User can create multiple families
- **One-to-Many**: `List<UserShare>` - User can share with multiple others
- **One-to-Many**: `List<UserShare>` - User can be shared with by multiple others

### 2. Address Management

#### Address Entity
Represents physical addresses with geographical data:
- `addressLineOne` (String) - Primary address line
- `addressLineTwo` (String) - Secondary address line
- `addressLineThree` (Optional<String>) - Tertiary address line
- `country` (Country) - Associated country
- `state` (Optional<CountryState>) - Optional state/province

#### UserAddress (Junction Entity)
Many-to-many relationship between users and addresses:
- `user` (User) - Associated user
- `address` (Address) - Associated address
- `addressType` (AddressType) - Type of address (home, work, etc.)
- `startDate` (LocalDate) - When this address became active
- `endDate` (Optional<LocalDate>) - When this address became inactive

**Example Usage:**
```java
User user = new User("user123", "john@example.com", "John", "Doe", "+1234567890", gender, hash);
Address homeAddress = new Address("123 Main St", "Apt 4B", country);
UserAddress userHomeAddress = new UserAddress(user, homeAddress, homeAddressType, LocalDate.now());
```

### 3. Family Management

#### Family Entity
Represents family groups:
- `creator` (User) - User who created the family
- `familyName` (String) - Name of the family
- `dateStarted` (LocalDate) - When the family was created
- `dateEnded` (Optional<LocalDate>) - When the family was dissolved

#### FamilyMembership (Junction Entity)
Many-to-many relationship between users and families:
- `family` (Family) - Associated family
- `user` (User) - Associated user
- `membershipType` (FamilyMemberType) - Role in the family (parent, child, etc.)
- `whenJoined` (LocalDateTime) - When user joined the family
- `whenLeft` (Optional<LocalDateTime>) - When user left the family

**Example Usage:**
```java
Family family = new Family(creator, "Smith Family", LocalDate.now());
FamilyMembership membership = new FamilyMembership(family, user, parentType, LocalDateTime.now());
```

### 4. Subscription Management

#### SubscriptionType Entity
Represents available subscription plans:
- `subscriptionDesc` (String) - Description of the subscription
- `subscriptionCode` (String) - Unique code for the subscription
- `cost` (BigDecimal) - Cost of the subscription
- `frequency` (Frequency) - Billing frequency (monthly, yearly, etc.)
- `availableStartDate` (LocalDate) - When subscription becomes available
- `availableEndDate` (Optional<LocalDate>) - When subscription expires

#### UserSubscription Entity
Many-to-many relationship between users and subscriptions:
- `user` (User) - Associated user
- `subscriptionType` (SubscriptionType) - Associated subscription type
- `startDate` (LocalDate) - When subscription started
- `endDate` (Optional<LocalDate>) - When subscription ended

### 5. Sharing System

#### UserShare Entity
Represents sharing relationships between users:
- `sharedBy` (User) - User who is sharing
- `sharedWith` (User) - User who is being shared with
- `shareType` (ShareType) - Type of sharing relationship
- `whenShared` (LocalDateTime) - When the sharing occurred

## Reference Entities

### Lookup Tables
These entities provide reference data and categorization:

1. **Gender** - User gender classifications
2. **Country** - Geographical countries with phone prefixes
3. **CountryState** - States/provinces within countries
4. **AddressType** - Types of addresses (home, work, billing, etc.)
5. **FamilyMemberType** - Family member roles (parent, child, guardian, etc.)
6. **Frequency** - Billing frequencies (monthly, quarterly, yearly, etc.)
7. **ShareType** - Types of sharing relationships
8. **VersionInfo** - Component version tracking

## Design Patterns

### 1. Optional Fields
Uses `Optional<T>` for nullable fields to provide type safety:
```java
private Optional<String> middleNameInitial;
private Optional<LocalDate> endDate;
```

### 2. Bidirectional Relationships
Maintains bidirectional relationships for easy navigation:
```java
// User can access addresses
List<UserAddress> addresses;

// Address can access users
List<UserAddress> userAddresses;
```

### 3. Junction Entities
Uses junction entities for many-to-many relationships with additional data:
```java
// UserAddress contains relationship data
public class UserAddress {
    private User user;
    private Address address;
    private AddressType addressType;
    private LocalDate startDate;
    private Optional<LocalDate> endDate;
}
```

### 4. Immutable Design
All entities use proper encapsulation with getters and setters for flexibility.

## Usage Examples

### Creating a User with Addresses
```java
// Create user
User user = new User("user123", "john@example.com", "John", "Doe", "+1234567890", gender, hash);

// Create addresses
Address homeAddress = new Address("123 Main St", "Apt 4B", country);
Address workAddress = new Address("456 Business Ave", "Suite 100", country);

// Create user address relationships
UserAddress homeUserAddress = new UserAddress(user, homeAddress, homeAddressType, LocalDate.now());
UserAddress workUserAddress = new UserAddress(user, workAddress, workAddressType, LocalDate.now());

// Set relationships
user.setAddresses(Arrays.asList(homeUserAddress, workUserAddress));
```

### Managing Family Relationships
```java
// Create family
Family family = new Family(creator, "Smith Family", LocalDate.now());

// Add family members
FamilyMembership parentMembership = new FamilyMembership(family, parent, parentType, LocalDateTime.now());
FamilyMembership childMembership = new FamilyMembership(family, child, childType, LocalDateTime.now());

// Set relationships
family.setMemberships(Arrays.asList(parentMembership, childMembership));
```

### Subscription Management
```java
// Create subscription
SubscriptionType premiumPlan = new SubscriptionType("Premium Plan", "PREMIUM", 
    new BigDecimal("29.99"), monthlyFrequency, LocalDate.now());

// Assign to user
UserSubscription userSub = new UserSubscription(user, premiumPlan, LocalDate.now());
user.setSubscriptions(Arrays.asList(userSub));
```

## Data Validation

### Required Fields
- User: id, email, firstName, familyName, phone, gender, userHash
- Address: addressLineOne, addressLineTwo, country
- Family: creator, familyName, dateStarted

### Optional Fields
- User: middleNameInitial
- Address: addressLineThree, state
- Family: dateEnded
- All junction entities: end dates

### Validation Rules
1. **Email Format**: Should be valid email format
2. **Phone Format**: Should include country code
3. **Date Logic**: End dates should be after start dates
4. **Required Relationships**: Junction entities require all related entities

## Performance Considerations

### Lazy Loading
Consider implementing lazy loading for large collections:
```java
// Example with lazy loading
@Lazy
private List<UserAddress> addresses;
```

### Pagination
For large datasets, implement pagination:
```java
// Example pagination method
public List<UserAddress> getAddresses(int page, int size) {
    // Implementation for paginated results
}
```

### Caching
Consider caching frequently accessed reference data:
```java
// Example caching for reference entities
@Cacheable("genders")
public List<Gender> getAllGenders() {
    // Implementation
}
```

## Integration with CQRS

### Command Objects
Create command objects for data modifications:
```java
public class CreateUserCommand {
    private String email;
    private String firstName;
    private String familyName;
    private String phone;
    private Integer genderId;
    private List<CreateAddressCommand> addresses;
}
```

### Query Objects
Create query objects for data retrieval:
```java
public class UserQuery {
    private String userId;
    private String email;
    private Boolean includeAddresses;
    private Boolean includeFamilies;
    private Boolean includeSubscriptions;
}
```

### DTOs
Create DTOs for API responses:
```java
public class UserDTO {
    private String id;
    private String email;
    private String fullName;
    private List<AddressDTO> addresses;
    private List<FamilyDTO> families;
}
```

## Testing

### Unit Tests
Create comprehensive unit tests for all entities:
```java
@Test
public void testUserCreation() {
    User user = new User("user123", "test@example.com", "John", "Doe", "+1234567890", gender, hash);
    assertEquals("user123", user.getId());
    assertEquals("test@example.com", user.getEmail());
}
```

### Integration Tests
Test entity relationships and database operations:
```java
@Test
public void testUserAddressRelationship() {
    User user = createUser();
    Address address = createAddress();
    UserAddress userAddress = new UserAddress(user, address, addressType, LocalDate.now());
    
    user.setAddresses(Arrays.asList(userAddress));
    assertEquals(1, user.getAddresses().size());
}
```

## Future Enhancements

### Potential Improvements
1. **Audit Trail**: Add created/updated timestamps to all entities
2. **Soft Deletes**: Implement soft delete functionality
3. **Versioning**: Add optimistic locking for concurrent modifications
4. **Validation Annotations**: Add Bean Validation annotations
5. **Builder Pattern**: Implement builder pattern for complex entity creation

### Extension Points
1. **Custom Validators**: Add custom validation logic
2. **Event Publishing**: Publish domain events on entity changes
3. **Caching Strategy**: Implement sophisticated caching
4. **Audit Logging**: Add comprehensive audit logging

---

**Note**: These model classes provide a solid foundation for the MTK Backend system. They follow Java best practices and provide a clean, maintainable interface for working with the database schema. 