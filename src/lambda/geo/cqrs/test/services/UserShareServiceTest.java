package com.mindthekid.geo.cqrs.test.services;

import com.mindthekid.models.UserShare;
import com.mindthekid.services.data.UserShareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserShareServiceTest {
    private UserShareService service;
    private EntityManager em;
    private EntityTransaction tx;
    private EntityManagerFactory emf;

    @BeforeEach
    public void setUp() {
        service = new UserShareService();
        em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        emf = mock(EntityManagerFactory.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    public void testCreate() {
        UserShare obj = new UserShare();
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            doNothing().when(em).persist(obj);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            UserShare result = service.create(obj);
            verify(em).persist(obj);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
            assertEquals(obj, result);
        }
    }

    @Test
    public void testDeleteDeep() {
        UserShare obj = new UserShare();
        obj.setId(6L);
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            when(em.find(UserShare.class, 6L)).thenReturn(obj);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            doNothing().when(em).remove(obj);
            service.delete(6L, true);
            verify(em).remove(obj);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
        }
    }
} 