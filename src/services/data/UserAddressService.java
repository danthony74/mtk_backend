package com.mindthekid.services.data;

import com.mindthekid.models.UserAddress;
import javax.persistence.EntityManager;

public class UserAddressService extends BaseService<UserAddress, Long> {
    public UserAddressService() {
        super(UserAddress.class);
    }

    @Override
    protected void deepDelete(EntityManager em, UserAddress userAddress) {
        // No further dependencies for UserAddress
    }
} 