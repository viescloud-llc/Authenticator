package vincentcorp.vshop.Authenticator.service;

import org.springframework.stereotype.Service;
import com.vincent.inc.viesspringutils.service.ViesService;
import com.vincent.inc.viesspringutils.util.DatabaseCall;
import vincentcorp.vshop.Authenticator.dao.RoleDao;
import vincentcorp.vshop.Authenticator.model.Role;

@Service
public class RoleService extends ViesService<Role, Integer, RoleDao> {

    public RoleService(DatabaseCall<Role, Integer> databaseCall, RoleDao repositoryDao) {
        super(databaseCall, repositoryDao);
    }

    @Override
    protected Role newEmptyObject() {
        return new Role();
    }
    
}