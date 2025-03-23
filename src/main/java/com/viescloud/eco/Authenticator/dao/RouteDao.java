package com.viescloud.eco.Authenticator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viescloud.eco.Authenticator.model.Route;

public interface RouteDao extends JpaRepository<Route, Long> {
	public Route findByPath(String path);
	public List<Route> findAllByPath(String path);

	public Route findBySecure(boolean secure);
	public List<Route> findAllBySecure(boolean secure);
}