package vincentcorp.vshop.Authenticator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viescloud.llc.viesspringutils.controller.ViesController;

import vincentcorp.vshop.Authenticator.model.Role;
import vincentcorp.vshop.Authenticator.service.RoleService;

@RestController
@RequestMapping("/roles")
class RoleController extends ViesController<Integer, Role, RoleService> {

    public RoleController(RoleService service) {
        super(service);
    }
}