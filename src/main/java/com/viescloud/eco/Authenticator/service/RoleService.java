package com.viescloud.eco.Authenticator.service;

import org.springframework.stereotype.Service;

import com.viescloud.eco.Authenticator.dao.RoleDao;
import com.viescloud.eco.Authenticator.model.Role;
import com.viescloud.eco.viesspringutils.repository.DatabaseCall;
import com.viescloud.eco.viesspringutils.service.ViesService;

@Service
public class RoleService extends ViesService<Long, Role, RoleDao> {

    public RoleService(DatabaseCall<Long, Role> databaseCall, RoleDao roleDao) {
        super(databaseCall, roleDao);
    }

    @Override
    protected Role newEmptyObject() {
        return new Role();
    }

    @Override
    public Long getIdFieldValue(Role object) {
        return object.getId();
    }
    
}