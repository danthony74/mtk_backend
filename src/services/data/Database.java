package com.mindthekid.services.data;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public final class Database {
    private static final EntityManagerFactory emf = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        Map<String, String> props = new HashMap<>();
        props.put("javax.persistence.jdbc.url", System.getenv("DB_URL"));
        props.put("javax.persistence.jdbc.user", System.getenv("DB_USER"));
        props.put("javax.persistence.jdbc.password", System.getenv("DB_PASSWORD"));
        return Persistence.createEntityManagerFactory("mtkPU", props);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void close() {
        emf.close();
    }
} 