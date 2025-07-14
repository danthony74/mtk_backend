package com.mindthekid.services.data;

import com.mindthekid.models.FamilyMemberType;
import javax.persistence.EntityManager;

public class FamilyMemberTypeService extends BaseService<FamilyMemberType, Long> {
    public FamilyMemberTypeService() {
        super(FamilyMemberType.class);
    }

    @Override
    protected void deepDelete(EntityManager em, FamilyMemberType familyMemberType) {
        // No further dependencies for FamilyMemberType
    }
} 