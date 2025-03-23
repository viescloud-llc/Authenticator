package com.viescloud.eco.Authenticator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viescloud.eco.Authenticator.model.Role;

public interface RoleDao extends JpaRepository<Role, Long> {
	public Role findByName(String name);
	public List<Role> findAllByName(String name);

	public Role findByLevel(int level);
	public List<Role> findAllByLevel(int level);
}