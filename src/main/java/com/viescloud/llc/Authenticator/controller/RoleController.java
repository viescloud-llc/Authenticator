package com.viescloud.llc.Authenticator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viescloud.llc.Authenticator.model.Role;
import com.viescloud.llc.Authenticator.service.RoleService;
import com.viescloud.llc.viesspringutils.controller.ViesController;

@RestController
@RequestMapping("/roles")
class RoleController extends ViesController<Long, Role, RoleService> {

    public RoleController(RoleService service) {
        super(service);
    }
}