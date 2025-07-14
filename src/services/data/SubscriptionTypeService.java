package com.mindthekid.services.data;

import com.mindthekid.models.SubscriptionType;
import javax.persistence.EntityManager;

public class SubscriptionTypeService extends BaseService<SubscriptionType, Long> {
    public SubscriptionTypeService() {
        super(SubscriptionType.class);
    }

    @Override
    protected void deepDelete(EntityManager em, SubscriptionType subscriptionType) {
        // No further dependencies for SubscriptionType
    }
} 