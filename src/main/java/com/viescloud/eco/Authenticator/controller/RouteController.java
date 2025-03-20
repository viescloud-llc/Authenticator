package com.viescloud.eco.Authenticator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viescloud.eco.Authenticator.model.Route;
import com.viescloud.eco.Authenticator.service.RouteService;
import com.viescloud.eco.viesspringutils.controller.ViesController;

import java.util.List;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/routes")
class RouteController extends ViesController<Long, Route, RouteService> {

    public RouteController(RouteService service) {
        super(service);
    }

    @PutMapping("sync")
    public List<Route> syncRoute(@RequestBody List<Route> routes) {
        return this.service.syncRoute(routes);
    }
    
}