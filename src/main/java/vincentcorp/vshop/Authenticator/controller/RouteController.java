package vincentcorp.vshop.Authenticator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viescloud.llc.viesspringutils.controller.ViesController;

import vincentcorp.vshop.Authenticator.model.Route;
import vincentcorp.vshop.Authenticator.service.RouteService;

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