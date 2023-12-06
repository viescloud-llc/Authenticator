package vincentcorp.vshop.Authenticator.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.vincent.inc.viesspringutils.exception.HttpResponseThrowers;
import com.vincent.inc.viesspringutils.util.DatabaseUtils;
import com.vincent.inc.viesspringutils.util.ReflectionUtils;

import org.springframework.data.domain.Example;
import vincentcorp.vshop.Authenticator.dao.RoleDao;
import vincentcorp.vshop.Authenticator.model.Role;

@Service
public class RoleService {
    public static final String HASH_KEY = "vincentcorp.vshop.Authenticator.service.RoleService";

    private DatabaseUtils<Role, Integer> databaseUtils;

    private RoleDao roleDao;

    public RoleService(DatabaseUtils<Role, Integer> databaseUtils, RoleDao roleDao) {
        this.databaseUtils = databaseUtils.init(roleDao, HASH_KEY);
        this.roleDao = roleDao;
    }

    public List<Role> getAll() {
        return this.roleDao.findAll();
    }

    public Role getById(int id) {
        Role role = this.databaseUtils.getAndExpire(id);

        if (ObjectUtils.isEmpty(role))
            HttpResponseThrowers.throwBadRequest("Role ID not found");

        return role;
    }

    public Role tryGetById(int id) {
        Role role = this.databaseUtils.getAndExpire(id);
        return role;
    }

    public List<Role> getAllByMatchAll(Role role) {
        Example<Role> example = ReflectionUtils.getMatchAllMatcher(role);
        return this.roleDao.findAll(example);
    }

    public List<Role> getAllByMatchAny(Role role) {
        Example<Role> example = ReflectionUtils.getMatchAnyMatcher(role);
        return this.roleDao.findAll(example);
    }

    public List<Role> getAllByMatchAll(Role role, String matchCase) {
        Example<Role> example = ReflectionUtils.getMatchAllMatcher(role, matchCase);
        return this.roleDao.findAll(example);
    }

    public List<Role> getAllByMatchAny(Role role, String matchCase) {
        Example<Role> example = ReflectionUtils.getMatchAnyMatcher(role, matchCase);
        return this.roleDao.findAll(example);
    }

    public Role createRole(Role role) {
        this.databaseUtils.saveAndExpire(role);
        return role;
    }

    public Role modifyRole(int id, Role role) {
        Role oldRole = this.getById(id);

        ReflectionUtils.replaceValue(oldRole, role);

        oldRole = this.databaseUtils.saveAndExpire(oldRole);

        return oldRole;
    }

    public Role patchRole(int id, Role role) {
        Role oldRole = this.getById(id);

        ReflectionUtils.patchValue(oldRole, role);

        oldRole = this.databaseUtils.saveAndExpire(oldRole);

        return oldRole;
    }

    public void deleteRole(int id) {
        this.databaseUtils.deleteById(id);
    }
}