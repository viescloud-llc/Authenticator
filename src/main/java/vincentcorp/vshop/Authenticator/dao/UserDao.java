package vincentcorp.vshop.Authenticator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vincentcorp.vshop.Authenticator.model.User;

public interface UserDao extends JpaRepository<User, Integer>
{
	public User findBySub(String sub);
	public List<User> findAllBySub(String sub);

	public User findByEmail(String email);
	public List<User> findAllByEmail(String email);

	public User findByName(String name);
	public List<User> findAllByName(String name);

	public User findByUsername(String username);
	public List<User> findAllByUsername(String username);

	public User findByPassword(String password);
	public List<User> findAllByPassword(String password);

	public User findByEnable(boolean enable);
	public List<User> findAllByEnable(boolean enable);

	@Query(value = "select * from User as user where user.sub = :sub or user.email = :email", nativeQuery = true)
	public List<User> getAllByMatchAnySubOrEmail(@Param("sub") String sub, @Param("email") String email);

	@Query(value = "select * from User as user where user.username = :username and user.password = :password", nativeQuery = true)
	public List<User> getAllByMatchAll(@Param("username") String username, @Param("password") String password);

	@Query(value = "select * from User as user where user.username = :username or user.password = :password", nativeQuery = true)
	public List<User> getAllByMatchAny(@Param("username") String username, @Param("password") String password);

	@Query(value = "SELECT max(id) as maxId FROM user;", nativeQuery = true)
	public int getMaxId();
}