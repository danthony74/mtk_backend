package com.mindthekid.services.data;

import com.mindthekid.models.ShareType;
import javax.persistence.EntityManager;

public class ShareTypeService extends BaseService<ShareType, Long> {
    public ShareTypeService() {
        super(ShareType.class);
    }

    @Override
    protected void deepDelete(EntityManager em, ShareType shareType) {
        // No further dependencies for ShareType
    }
} 