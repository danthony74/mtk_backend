package com.mindthekid.services.data;

import com.mindthekid.models.User;
import javax.persistence.EntityManager;

public class UserService extends BaseService<User, Long> {
    public UserService() {
        super(User.class);
    }

    @Override
    protected void deepDelete(EntityManager em, User user) {
        // Example: delete related addresses, subscriptions, shares, etc.
        // (Stub: implement actual logic or call other services as needed)
        em.createQuery("DELETE FROM UserAddress ua WHERE ua.user = :user")
          .setParameter("user", user)
          .executeUpdate();
        em.createQuery("DELETE FROM UserSubscription us WHERE us.user = :user")
          .setParameter("user", user)
          .executeUpdate();
        em.createQuery("DELETE FROM UserShare us WHERE us.user = :user")
          .setParameter("user", user)
          .executeUpdate();
        // Add more as needed for other dependencies
    }
} 