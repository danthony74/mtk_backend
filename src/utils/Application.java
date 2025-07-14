package com.mindthekid.utils;

import java.time.format.DateTimeFormatter;

/**
 * Application utility class for holding common constants and date formats.
 * This class cannot be instantiated or extended.
 */
public final class Application {
    // Prevent instantiation
    private Application() {
        throw new AssertionError("Cannot instantiate Application class");
    }

    // Example: ISO 8601 date-time format
    public static final DateTimeFormatter ISO_DATE_TIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;

    // Example: Custom date format
    public static final DateTimeFormatter CUSTOM_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Example: dd/mon/yyyy format (e.g., 05/Jul/2024)
    public static final DateTimeFormatter UNIVERSAL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MMM/yyyy");

    // Example: Common application constant
    public static final String APP_NAME = "MindTheKidBackend";

    // Add more shared constants and utilities as needed
} 