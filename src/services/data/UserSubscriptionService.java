package com.mindthekid.services.data;

import com.mindthekid.models.UserSubscription;
import javax.persistence.EntityManager;

public class UserSubscriptionService extends BaseService<UserSubscription, Long> {
    public UserSubscriptionService() {
        super(UserSubscription.class);
    }

    @Override
    protected void deepDelete(EntityManager em, UserSubscription userSubscription) {
        // No further dependencies for UserSubscription
    }
} 