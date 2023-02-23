package vincentcorp.vshop.Authenticator.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Example;

import vincentcorp.vshop.Authenticator.dao.RoleDao;
import vincentcorp.vshop.Authenticator.model.Role;
import vincentcorp.vshop.Authenticator.util.ReflectionUtils;

@Service
public class RoleService
{
    @Autowired
    private RoleDao roleDao;

    public List<Role> getAll()
    {
        return this.roleDao.findAll();
    }

    public Role getById(int id)
    {
        Optional<Role> role = this.roleDao.findById(id);

        if(role.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role ID not found");

        return role.get();
    }

    public List<Role> getAllByMatchAll(Role role)
    {
        Example<Role> example = (Example<Role>) ReflectionUtils.getMatchAllMatcher(role);
        return this.roleDao.findAll(example);
    }

    public List<Role> getAllByMatchAny(Role role)
    {
        Example<Role> example = (Example<Role>) ReflectionUtils.getMatchAnyMatcher(role);
        return this.roleDao.findAll(example);
    }

    public Role createRole(Role role)
    {
        role = this.roleDao.save(role);
        return role;
    }

    public Role modifyRole(int id, Role role)
    {
        Role oldRole = this.getById(id);

		oldRole.setName(role.getName());
		oldRole.setLevel(role.getLevel());


        oldRole = this.roleDao.save(oldRole);
        return oldRole;
    }

    public Role patchRole(int id, Role role)
    {
        Role oldRole = this.getById(id);

		oldRole.setName(role.getName() == null ? oldRole.getName() : role.getName());
		oldRole.setLevel(role.getLevel());


        oldRole = this.roleDao.save(oldRole);
        return oldRole;
    }

    public void deleteRole(int id)
    {
        this.roleDao.deleteById(id);
    }
}