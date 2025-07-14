package com.mindthekid.services.data;

import com.mindthekid.models.Address;
import javax.persistence.EntityManager;

public class AddressService extends BaseService<Address, Long> {
    public AddressService() {
        super(Address.class);
    }

    @Override
    protected void deepDelete(EntityManager em, Address address) {
        // Example: delete related UserAddress records
        em.createQuery("DELETE FROM UserAddress ua WHERE ua.address = :address")
          .setParameter("address", address)
          .executeUpdate();
        // Add more as needed for other dependencies
    }
} 