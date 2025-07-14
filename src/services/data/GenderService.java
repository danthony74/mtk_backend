package com.mindthekid.services.data;

import com.mindthekid.models.Gender;
import javax.persistence.EntityManager;

public class GenderService extends BaseService<Gender, Long> {
    public GenderService() {
        super(Gender.class);
    }

    @Override
    protected void deepDelete(EntityManager em, Gender gender) {
        // No further dependencies for Gender
    }
} 