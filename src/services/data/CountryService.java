package com.mindthekid.services.data;

import com.mindthekid.models.Country;
import javax.persistence.EntityManager;

public class CountryService extends BaseService<Country, Long> {
    public CountryService() {
        super(Country.class);
    }

    @Override
    protected void deepDelete(EntityManager em, Country country) {
        // Delete related CountryStates
        em.createQuery("DELETE FROM CountryState cs WHERE cs.country = :country")
          .setParameter("country", country)
          .executeUpdate();
        // Add more as needed for other dependencies
    }
} 