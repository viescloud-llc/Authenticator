package vincentcorp.vshop.Authenticator.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Example;

import vincentcorp.vshop.Authenticator.dao.RouteDao;
import vincentcorp.vshop.Authenticator.model.Route;
import vincentcorp.vshop.Authenticator.util.ReflectionUtils;

@Service
public class RouteService
{
    @Autowired
    private RouteDao routeDao;

    public List<Route> getAll()
    {
        return this.routeDao.findAll();
    }

    public Route getById(int id)
    {
        Optional<Route> route = this.routeDao.findById(id);

        if(route.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Route ID not found");

        return route.get();
    }

    public List<Route> getAllByMatchAll(Route route)
    {
        Example<Route> example = (Example<Route>) ReflectionUtils.getMatchAllMatcher(route);
        return this.routeDao.findAll(example);
    }

    public List<Route> getAllByMatchAny(Route route)
    {
        Example<Route> example = (Example<Route>) ReflectionUtils.getMatchAnyMatcher(route);
        return this.routeDao.findAll(example);
    }

    public Route createRoute(Route route)
    {
        route = this.routeDao.save(route);
        return route;
    }

    public Route modifyRoute(int id, Route route)
    {
        Route oldRoute = this.getById(id);

		oldRoute.setPath(route.getPath());
		oldRoute.setSecure(route.isSecure());
		oldRoute.setRoles(route.getRoles());


        oldRoute = this.routeDao.save(oldRoute);
        return oldRoute;
    }

    public Route patchRoute(int id, Route route)
    {
        Route oldRoute = this.getById(id);

		oldRoute.setPath(route.getPath() == null ? oldRoute.getPath() : route.getPath());
		oldRoute.setSecure(route.isSecure());
		oldRoute.setRoles(route.getRoles() == null ? oldRoute.getRoles() : route.getRoles());


        oldRoute = this.routeDao.save(oldRoute);
        return oldRoute;
    }

    public void deleteRoute(int id)
    {
        this.routeDao.deleteById(id);
    }
}