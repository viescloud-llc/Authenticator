package vincentcorp.vshop.Authenticator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vincentcorp.vshop.Authenticator.model.User;

public interface UserDao extends JpaRepository<User, Integer> 
{
    public User findByUsername(String username);
    public List<User> findAllByUsername(String username);
}
