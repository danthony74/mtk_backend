package com.mindthekid.services.data;

import com.mindthekid.models.Frequency;
import javax.persistence.EntityManager;

public class FrequencyService extends BaseService<Frequency, Long> {
    public FrequencyService() {
        super(Frequency.class);
    }

    @Override
    protected void deepDelete(EntityManager em, Frequency frequency) {
        // No further dependencies for Frequency
    }
} 