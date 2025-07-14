package com.mindthekid.services.data;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.function.Consumer;

public abstract class BaseService<T, ID> {
    protected final Class<T> entityClass;

    protected BaseService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T create(T entity) {
        return executeInTransaction(em -> em.persist(entity), entity);
    }

    public T update(T entity) {
        return executeInTransaction(em -> em.merge(entity), entity);
    }

    public T findById(ID id) {
        EntityManager em = Database.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = Database.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(ID id, boolean deep) {
        EntityManager em = Database.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                if (deep) {
                    deepDelete(em, entity);
                }
                em.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Override in subclasses for cascading delete logic
    protected void deepDelete(EntityManager em, T entity) {}

    private T executeInTransaction(Consumer<EntityManager> action, T entity) {
        EntityManager em = Database.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.accept(em);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
} 