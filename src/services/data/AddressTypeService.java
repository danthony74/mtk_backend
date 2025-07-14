package com.mindthekid.services.data;

import com.mindthekid.models.AddressType;
import javax.persistence.EntityManager;

public class AddressTypeService extends BaseService<AddressType, Long> {
    public AddressTypeService() {
        super(AddressType.class);
    }

    @Override
    protected void deepDelete(EntityManager em, AddressType addressType) {
        // No further dependencies for AddressType
    }
} 