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
import org.springframework.web.ErrorResponseException;

import jakarta.ws.rs.QueryParam;
import io.swagger.v3.oas.annotations.Operation;

import vincentcorp.vshop.Authenticator.model.Role;
import vincentcorp.vshop.Authenticator.service.RoleService;
import vincentcorp.vshop.Authenticator.util.splunk.Splunk;

@RestController
@RequestMapping("/roles")
class RoleController
{
    @Autowired
    RoleService roleService;
    
    @Operation(summary = "Get a list of all Role")
    @GetMapping
    public ResponseEntity<List<Role>> getAll()
    {
        try
        {
            List<Role> roles = roleService.getAll();

            if (roles.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(roles, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get Role base on id in path variable")
    @GetMapping("{id}")
    public ResponseEntity<Role> getById(@PathVariable("id") int id)
    {
        try
        {
            Role role = roleService.getById(id);

            return new ResponseEntity<>(role, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get a list of all Role that match all information base on query parameter")
    @GetMapping("match_all")
    public ResponseEntity<List<Role>> matchAll(@QueryParam("role") Role role)
    {
        try
        {
            List<Role> roles = this.roleService.getAllByMatchAll(role);

            if (roles.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(roles, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get a list of all Role that match any information base on query parameter")
    @GetMapping("match_any")
    public ResponseEntity<List<Role>> matchAny(@QueryParam("role") Role role)
    {
        try
        {
            List<Role> roles = this.roleService.getAllByMatchAny(role);

            if (roles.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(roles, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create a new Role")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Role> create(@RequestBody Role role)
    {
        try
        {
            Role savedRole = roleService.createRole(role);
            return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Operation(summary = "Modify an Role base on id in path variable")
    @PutMapping("{id}")
    public ResponseEntity<Role> update(@PathVariable("id") int id, @RequestBody Role role)
    {
        try
        {
            role = this.roleService.modifyRole(id, role);

            return new ResponseEntity<>(role, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Patch an Role base on id in path variable")
    @PatchMapping("{id}")
    public ResponseEntity<Role> patch(@PathVariable("id") int id, @RequestBody Role role)
    {
        try
        {
            role = this.roleService.patchRole(id, role);

            return new ResponseEntity<>(role, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete an Role base on id in path variable")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id)
    {
        try
        {
            roleService.deleteRole(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}