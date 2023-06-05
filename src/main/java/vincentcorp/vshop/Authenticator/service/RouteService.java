package vincentcorp.vshop.Authenticator.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import org.springframework.data.domain.Example;
import vincentcorp.vshop.Authenticator.dao.RouteDao;
import vincentcorp.vshop.Authenticator.model.Route;
import vincentcorp.vshop.Authenticator.util.DatabaseUtils;
import vincentcorp.vshop.Authenticator.util.ReflectionUtils;
import vincentcorp.vshop.Authenticator.util.Http.HttpResponseThrowers;

@Service
public class RouteService {
    public static final String HASH_KEY = "vincentcorp.vshop.Authenticator.service.RouteService";

    private DatabaseUtils<Route, Integer> databaseUtils;

    private RouteDao routeDao;

    public RouteService(DatabaseUtils<Route, Integer> databaseUtils, RouteDao routeDao) {
        this.databaseUtils = databaseUtils.init(routeDao, HASH_KEY);
        this.routeDao = routeDao;
    }

    public List<Route> getAll() {
        return this.routeDao.findAll();
    }

    public Route getById(int id) {
        Route route = this.databaseUtils.get(id);

        if (ObjectUtils.isEmpty(route))
            HttpResponseThrowers.throwBadRequest("Route ID not found");

        return route;
    }

    public List<Route> getAllByMatchAll(Route route) {
        Example<Route> example = ReflectionUtils.getMatchAllMatcher(route);
        return this.routeDao.findAll(example);
    }

    public List<Route> getAllByMatchAny(Route route) {
        Example<Route> example = ReflectionUtils.getMatchAnyMatcher(route);
        return this.routeDao.findAll(example);
    }

    public Route createRoute(Route route) {
        this.databaseUtils.saveAndExpire(route);
        return route;
    }

    public Route modifyRoute(int id, Route route) {
        Route oldRoute = this.getById(id);

        ReflectionUtils.replaceValue(oldRoute, route);

        oldRoute = this.databaseUtils.saveAndExpire(oldRoute);

        return oldRoute;
    }

    public Route patchRoute(int id, Route route) {
        Route oldRoute = this.getById(id);

        ReflectionUtils.patchValue(oldRoute, route);

        oldRoute = this.databaseUtils.saveAndExpire(oldRoute);

        return oldRoute;
    }

    public void deleteRoute(int id) {
        this.databaseUtils.deleteById(id);
    }
}