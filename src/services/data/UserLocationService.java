package com.mindthekid.services.data;

import com.mindthekid.models.UserLocation;
import javax.persistence.EntityManager;

public class UserLocationService extends BaseService<UserLocation, Long> {
    public UserLocationService() {
        super(UserLocation.class);
    }

    @Override
    protected void deepDelete(EntityManager em, UserLocation userLocation) {
        // No further dependencies for UserLocation
    }
} 