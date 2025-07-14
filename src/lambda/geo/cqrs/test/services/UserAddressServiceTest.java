package com.mindthekid.geo.cqrs.test.services;

import com.mindthekid.models.UserAddress;
import com.mindthekid.services.data.UserAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserAddressServiceTest {
    private UserAddressService service;
    private EntityManager em;
    private EntityTransaction tx;
    private EntityManagerFactory emf;

    @BeforeEach
    public void setUp() {
        service = new UserAddressService();
        em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        emf = mock(EntityManagerFactory.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    public void testCreate() {
        UserAddress obj = new UserAddress();
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            doNothing().when(em).persist(obj);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            UserAddress result = service.create(obj);
            verify(em).persist(obj);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
            assertEquals(obj, result);
        }
    }

    @Test
    public void testDeleteDeep() {
        UserAddress obj = new UserAddress();
        obj.setId(4L);
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            when(em.find(UserAddress.class, 4L)).thenReturn(obj);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            doNothing().when(em).remove(obj);
            service.delete(4L, true);
            verify(em).remove(obj);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
        }
    }
} 