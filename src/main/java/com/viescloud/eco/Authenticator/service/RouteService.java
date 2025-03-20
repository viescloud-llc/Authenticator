package com.viescloud.eco.Authenticator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viescloud.eco.Authenticator.dao.RouteDao;
import com.viescloud.eco.Authenticator.model.Role;
import com.viescloud.eco.Authenticator.model.Route;
import com.viescloud.eco.viesspringutils.repository.DatabaseCall;
import com.viescloud.eco.viesspringutils.service.ViesService;

@Service
public class RouteService extends ViesService<Long, Route, RouteDao> {

    @Autowired
    private RoleService roleService;

    public RouteService(DatabaseCall<Long, Route> databaseCall, RouteDao routeDao) {
        super(databaseCall, routeDao);
    }

    @Override
    protected Route newEmptyObject() {
        return new Route();
    }
    
    public List<Route> syncRoute(List<Route> newRoutes) {
        List<Route> currentRoutes = new ArrayList<>();
        currentRoutes.addAll(this.getAll());
        List<Role> roles = new ArrayList<>();
        roles.addAll(this.roleService.getAll());

        newRoutes.forEach(route -> {
            List<Role> userRoles = route.getRoles();
            List<Role> assignRoles = new ArrayList<>();
            userRoles.forEach(r -> {
                List<Role> foundedRoles = roles.parallelStream().filter(e -> e.getName().equals(r.getName())).collect(Collectors.toList());
                if(foundedRoles.size() > 0) {
                    assignRoles.add(foundedRoles.get(0));
                }
                else {
                    r.setId(0L);
                    Role role = this.roleService.post(r);
                    assignRoles.add(role);
                    roles.add(role);
                }
            });
            route.setRoles(assignRoles);

            List<Route> foundedRoutes = currentRoutes.parallelStream().filter(e -> e.getPath().equals(route.getPath()) && e.getMethod().equals(route.getMethod())).collect(Collectors.toList());
            if(foundedRoutes.size() > 0) {
                Route foundedRoute = foundedRoutes.get(0);
                this.put(foundedRoute.getId(), route);
                currentRoutes.remove(foundedRoute);
            }
            else
                this.post(route);
        });

        currentRoutes.parallelStream().forEach(e -> this.delete(e.getId()));

        return this.getAll();
    }

    @Override
    public Long getIdFieldValue(Route object) {
        return object.getId();
    }
}