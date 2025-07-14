package com.mindthekid.services.data;

import com.mindthekid.models.UserShare;
import javax.persistence.EntityManager;

public class UserShareService extends BaseService<UserShare, Long> {
    public UserShareService() {
        super(UserShare.class);
    }

    @Override
    protected void deepDelete(EntityManager em, UserShare userShare) {
        // No further dependencies for UserShare
    }
} 