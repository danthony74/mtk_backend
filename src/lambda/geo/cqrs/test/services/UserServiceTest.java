package com.mindthekid.geo.cqrs.test.services;

import com.mindthekid.models.User;
import com.mindthekid.services.data.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;
    private EntityManager em;
    private EntityTransaction tx;
    private EntityManagerFactory emf;

    @BeforeEach
    public void setUp() {
        userService = new UserService();
        em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        emf = mock(EntityManagerFactory.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            doNothing().when(em).persist(user);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            User result = userService.create(user);
            verify(em).persist(user);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
            assertEquals(user, result);
        }
    }

    @Test
    public void testFindById() {
        User user = new User();
        user.setId(1L);
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            when(em.find(User.class, 1L)).thenReturn(user);
            User found = userService.findById(1L);
            verify(em).find(User.class, 1L);
            verify(em).close();
            assertEquals(user, found);
        }
    }

    @Test
    public void testDeleteUserDeep() {
        User user = new User();
        user.setId(2L);
        try (MockedStatic<com.mindthekid.services.data.Database> db = Mockito.mockStatic(com.mindthekid.services.data.Database.class)) {
            db.when(com.mindthekid.services.data.Database::getEntityManagerFactory).thenReturn(emf);
            when(em.find(User.class, 2L)).thenReturn(user);
            doNothing().when(tx).begin();
            doNothing().when(tx).commit();
            doNothing().when(em).remove(user);
            // Mock deep delete queries
            when(em.createQuery(anyString())).thenReturn(mock(javax.persistence.Query.class));
            userService.delete(2L, true);
            verify(em).remove(user);
            verify(tx).begin();
            verify(tx).commit();
            verify(em).close();
        }
    }
} 