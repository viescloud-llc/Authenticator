package vincentcorp.vshop.Authenticator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vincentcorp.vshop.Authenticator.model.User;

public interface UserDao extends JpaRepository<User, Integer>
{
	public User findByUsername(String username);
	public List<User> findAllByUsername(String username);

	public User findByPassword(String password);
	public List<User> findAllByPassword(String password);

	@Query(value = "select * from User as user where user.username = :username and user.password = :password", nativeQuery = true)
	public List<User> getAllByMatch(@Param("username") String username, @Param("password") String password);

}