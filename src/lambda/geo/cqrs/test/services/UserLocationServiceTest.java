package com.mindthekid.geo.cqrs.test.services;

import com.mindthekid.models.UserLocation;
import com.mindthekid.services.data.UserLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserLocationServiceTest {
    private UserLocationService service;
    private EntityManager em;
    private EntityTransaction tx;
    private EntityManagerFactory emf;

    @BeforeEach
    public void setUp() {
        service = new UserLocationService();
        em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        emf = mock(EntityManagerFactory.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    public void testCreate() {
        UserLocation obj = new UserLocation();
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            doNothing().when(em).persist(obj);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            UserLocation result = service.create(obj);
            verify(em).persist(obj);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
            assertEquals(obj, result);
        }
    }

    @Test
    public void testDeleteDeep() {
        UserLocation obj = new UserLocation();
        obj.setId(7L);
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            when(em.find(UserLocation.class, 7L)).thenReturn(obj);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            doNothing().when(em).remove(obj);
            service.delete(7L, true);
            verify(em).remove(obj);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
        }
    }
} 