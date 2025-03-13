package vincentcorp.vshop.Authenticator.service;

import org.springframework.stereotype.Service;

import com.viescloud.llc.viesspringutils.repository.DatabaseCall;
import com.viescloud.llc.viesspringutils.service.ViesService;

import vincentcorp.vshop.Authenticator.dao.RoleDao;
import vincentcorp.vshop.Authenticator.model.Role;

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