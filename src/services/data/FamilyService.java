package com.mindthekid.services.data;

import com.mindthekid.models.Family;
import javax.persistence.EntityManager;

public class FamilyService extends BaseService<Family, Long> {
    public FamilyService() {
        super(Family.class);
    }

    @Override
    protected void deepDelete(EntityManager em, Family family) {
        // Delete related FamilyMemberships
        em.createQuery("DELETE FROM FamilyMembership fm WHERE fm.family = :family")
          .setParameter("family", family)
          .executeUpdate();
        // Add more as needed for other dependencies
    }
} 