package com.mindthekid.services.data;

import com.mindthekid.models.FamilyMembership;
import javax.persistence.EntityManager;

public class FamilyMembershipService extends BaseService<FamilyMembership, Long> {
    public FamilyMembershipService() {
        super(FamilyMembership.class);
    }

    @Override
    protected void deepDelete(EntityManager em, FamilyMembership familyMembership) {
        // No further dependencies for FamilyMembership
    }
} 