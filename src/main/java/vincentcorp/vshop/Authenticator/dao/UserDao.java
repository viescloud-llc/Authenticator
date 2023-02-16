package vincentcorp.vshop.Authenticator.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import vincentcorp.vshop.Authenticator.model.User;

public interface UserDao extends JpaRepository<User, Integer> 
{

}
