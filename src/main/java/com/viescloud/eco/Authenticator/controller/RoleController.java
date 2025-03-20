package com.viescloud.eco.Authenticator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viescloud.eco.Authenticator.model.Role;
import com.viescloud.eco.Authenticator.service.RoleService;
import com.viescloud.eco.viesspringutils.controller.ViesController;

@RestController
@RequestMapping("/roles")
class RoleController extends ViesController<Long, Role, RoleService> {

    public RoleController(RoleService service) {
        super(service);
    }
}