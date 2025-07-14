package com.mindthekid.geo.cqrs.test.services;

import com.mindthekid.models.User;
import com.mindthekid.services.data.UserService;

public class ServiceExample {
    public static void main(String[] args) {
        UserService userService = new UserService();

        // Create a new user
        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setEmail("alice@example.com");
        user = userService.create(user);
        System.out.println("Created user: " + user.getId());

        // Read user
        User found = userService.findById(user.getId());
        System.out.println("Found user: " + found.getFirstName());

        // Update user
        found.setLastName("Johnson");
        userService.update(found);
        System.out.println("Updated user last name to: " + found.getLastName());

        // Delete user (deep delete)
        userService.delete(found.getId(), true);
        System.out.println("Deleted user with deep cascade");
    }
} 