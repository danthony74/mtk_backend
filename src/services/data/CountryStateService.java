package com.mindthekid.services.data;

import com.mindthekid.models.CountryState;
import javax.persistence.EntityManager;

public class CountryStateService extends BaseService<CountryState, Long> {
    public CountryStateService() {
        super(CountryState.class);
    }

    @Override
    protected void deepDelete(EntityManager em, CountryState countryState) {
        // No further dependencies for CountryState
    }
} 