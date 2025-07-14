package com.mindthekid.geo.cqrs.test.services;

import com.mindthekid.models.Address;
import com.mindthekid.services.data.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddressServiceTest {
    private AddressService addressService;
    private EntityManager em;
    private EntityTransaction tx;
    private EntityManagerFactory emf;

    @BeforeEach
    public void setUp() {
        addressService = new AddressService();
        em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        emf = mock(EntityManagerFactory.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    public void testCreateAddress() {
        Address address = new Address();
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            doNothing().when(em).persist(address);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            Address result = addressService.create(address);
            verify(em).persist(address);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
            assertEquals(address, result);
        }
    }

    @Test
    public void testDeleteAddressDeep() {
        Address address = new Address();
        address.setId(3L);
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            when(em.find(Address.class, 3L)).thenReturn(address);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            doNothing().when(em).remove(address);
            // Mock deep delete queries
            when(em.createQuery(anyString())).thenReturn(mock(javax.persistence.Query.class));
            addressService.delete(3L, true);
            verify(em).remove(address);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
        }
    }
} 