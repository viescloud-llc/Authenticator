package com.viescloud.eco.Authenticator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.viescloud.eco.Authenticator.model.User;

public interface UserDao extends JpaRepository<User, Long>
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

	@Query(value = "SELECT MAX(e.id) FROM User e")
	public Long getMaxId();
}