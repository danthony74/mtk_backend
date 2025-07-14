package com.mindthekid.geo.cqrs.test.services;

import com.mindthekid.models.UserSubscription;
import com.mindthekid.services.data.UserSubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserSubscriptionServiceTest {
    private UserSubscriptionService service;
    private EntityManager em;
    private EntityTransaction tx;
    private EntityManagerFactory emf;

    @BeforeEach
    public void setUp() {
        service = new UserSubscriptionService();
        em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        emf = mock(EntityManagerFactory.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    public void testCreate() {
        UserSubscription obj = new UserSubscription();
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            doNothing().when(em).persist(obj);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            UserSubscription result = service.create(obj);
            verify(em).persist(obj);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
            assertEquals(obj, result);
        }
    }

    @Test
    public void testDeleteDeep() {
        UserSubscription obj = new UserSubscription();
        obj.setId(5L);
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            when(em.find(UserSubscription.class, 5L)).thenReturn(obj);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            doNothing().when(em).remove(obj);
            service.delete(5L, true);
            verify(em).remove(obj);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
        }
    }
} 