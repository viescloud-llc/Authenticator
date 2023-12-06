package vincentcorp.vshop.Authenticator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.viesspringutils.controller.ViesController;

import vincentcorp.vshop.Authenticator.model.Route;
import vincentcorp.vshop.Authenticator.service.RouteService;

@RestController
@RequestMapping("/routes")
class RouteController extends ViesController<Route, Integer, RouteService> {

    public RouteController(RouteService service) {
        super(service);
    }
}