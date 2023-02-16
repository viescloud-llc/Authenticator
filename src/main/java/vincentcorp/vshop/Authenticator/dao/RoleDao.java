package vincentcorp.vshop.Authenticator.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import vincentcorp.vshop.Authenticator.model.Role;

public interface RoleDao extends JpaRepository<Role, Integer>  
{
    public Role findByName(String name);
}
