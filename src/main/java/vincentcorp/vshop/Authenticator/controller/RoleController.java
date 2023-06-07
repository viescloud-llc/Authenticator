package vincentcorp.vshop.Authenticator.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.ws.rs.QueryParam;
import io.swagger.v3.oas.annotations.Operation;

import vincentcorp.vshop.Authenticator.model.Role;
import vincentcorp.vshop.Authenticator.service.RoleService;

@RestController
@RequestMapping("/roles")
class RoleController {
    @Autowired
    RoleService roleService;

    @Operation(summary = "Get a list of all Role")
    @GetMapping
    public ResponseEntity<List<Role>> getAll() {
        List<Role> roles = roleService.getAll();

        if (roles.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Operation(summary = "Get Role base on id in path variable")
    @GetMapping("{id}")
    public ResponseEntity<Role> getById(@PathVariable("id") int id) {
        Role role = roleService.getById(id);

        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @Operation(summary = "Get a list of all Role that match all information base on query parameter")
    @GetMapping("match_all")
    public ResponseEntity<List<Role>> matchAll(@QueryParam("role") Role role) {
        List<Role> roles = this.roleService.getAllByMatchAll(role);

        if (roles.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Operation(summary = "Get a list of all Role that match any information base on query parameter")
    @GetMapping("match_any")
    public ResponseEntity<List<Role>> matchAny(@QueryParam("role") Role role) {
        List<Role> roles = this.roleService.getAllByMatchAny(role);

        if (roles.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Operation(summary = "Get a list of all Role that match all information base on query parameter and match case")
    @GetMapping("match_all/{matchCase}")
    public ResponseEntity<List<Role>> matchAll(@QueryParam("role") Role role,
            @PathVariable("matchCase") String matchCase) {
        List<Role> roles = this.roleService.getAllByMatchAll(role, matchCase);

        if (roles.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Operation(summary = "Get a list of all Role that match any information base on query parameter and match case")
    @GetMapping("match_any/{matchCase}")
    public ResponseEntity<List<Role>> matchAny(@QueryParam("role") Role role,
            @PathVariable("matchCase") String matchCase) {
        List<Role> roles = this.roleService.getAllByMatchAny(role, matchCase);

        if (roles.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Operation(summary = "Create a new Role")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Role> create(@RequestBody Role role) {
        Role savedRole = roleService.createRole(role);
        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);

    }

    @Operation(summary = "Modify a Role base on id in path variable")
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Role> update(@PathVariable("id") int id, @RequestBody Role role) {
        role = this.roleService.modifyRole(id, role);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @Operation(summary = "Patch a Role base on id in path variable")
    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Role> patch(@PathVariable("id") int id, @RequestBody Role role) {
        role = this.roleService.patchRole(id, role);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @Operation(summary = "Delete a Role base on id in path variable")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}