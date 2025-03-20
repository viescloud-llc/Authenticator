package com.viescloud.llc.Authenticator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.viescloud.llc.Authenticator.model.Role;

public interface RoleDao extends JpaRepository<Role, Long> {
	public Role findByName(String name);
	public List<Role> findAllByName(String name);

	public Role findByLevel(int level);
	public List<Role> findAllByLevel(int level);

	@Query(value = "select * from Role as role where role.name = :name and role.level = :level", nativeQuery = true)
	public List<Role> getAllByMatchAll(@Param("name") String name, @Param("level") int level);

	@Query(value = "select * from Role as role where role.name = :name or role.level = :level", nativeQuery = true)
	public List<Role> getAllByMatchAny(@Param("name") String name, @Param("level") int level);
}