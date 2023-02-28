package vincentcorp.vshop.Authenticator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vincentcorp.vshop.Authenticator.model.Route;

public interface RouteDao extends JpaRepository<Route, Integer>
{
	public Route findByPath(String path);
	public List<Route> findAllByPath(String path);

	public Route findBySecure(boolean secure);
	public List<Route> findAllBySecure(boolean secure);

	@Query(value = "select * from Route as route where route.path = :path and route.secure = :secure", nativeQuery = true)
	public List<Route> getAllByMatchAll(@Param("path") String path, @Param("secure") boolean secure);

	@Query(value = "select * from Route as route where route.path = :path or route.secure = :secure", nativeQuery = true)
	public List<Route> getAllByMatchAny(@Param("path") String path, @Param("secure") boolean secure);


}