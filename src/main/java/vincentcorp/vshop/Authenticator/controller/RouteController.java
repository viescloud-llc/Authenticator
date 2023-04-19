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

import vincentcorp.vshop.Authenticator.model.Route;
import vincentcorp.vshop.Authenticator.service.RouteService;
import vincentcorp.vshop.Authenticator.util.splunk.Splunk;

@RestController
@RequestMapping("/routes")
class RouteController
{
    @Autowired
    RouteService routeService;

    @Operation(summary = "Get a list of all Route")
    @GetMapping
    public ResponseEntity<List<Route>> getAll()
    {
        try
        {
            List<Route> routes = routeService.getAll();

            if (routes.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(routes, HttpStatus.OK);
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

    @Operation(summary = "Get Route base on id in path variable")
    @GetMapping("{id}")
    public ResponseEntity<Route> getById(@PathVariable("id") int id)
    {
        try
        {
            Route route = routeService.getById(id);

            return new ResponseEntity<>(route, HttpStatus.OK);
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

    @Operation(summary = "Get a list of all Route that match all information base on query parameter")
    @GetMapping("match_all")
    public ResponseEntity<List<Route>> matchAll(@QueryParam("route") Route route)
    {
        try
        {
            List<Route> routes = this.routeService.getAllByMatchAll(route);

            if (routes.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(routes, HttpStatus.OK);
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

    @Operation(summary = "Get a list of all Route that match any information base on query parameter")
    @GetMapping("match_any")
    public ResponseEntity<List<Route>> matchAny(@QueryParam("route") Route route)
    {
        try
        {
            List<Route> routes = this.routeService.getAllByMatchAny(route);

            if (routes.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(routes, HttpStatus.OK);
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

    @Operation(summary = "Create a new Route")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Route> create(@RequestBody Route route)
    {
        try
        {
            Route savedRoute = routeService.createRoute(route);
            return new ResponseEntity<>(savedRoute, HttpStatus.CREATED);
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

    @Operation(summary = "Modify an Route base on id in path variable")
    @PutMapping("{id}")
    public ResponseEntity<Route> update(@PathVariable("id") int id, @RequestBody Route route)
    {
        try
        {
            route = this.routeService.modifyRoute(id, route);

            return new ResponseEntity<>(route, HttpStatus.OK);
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

    @Operation(summary = "Modify an Route base on id in path variable")
    @PutMapping("default/route")
    public ResponseEntity<List<Route>> createDefaultRoute()
    {
        try
        {
            List<Route> routes = this.routeService.createDefaultRoute();

            return new ResponseEntity<>(routes, HttpStatus.OK);
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

    @Operation(summary = "Patch an Route base on id in path variable")
    @PatchMapping("{id}")
    public ResponseEntity<Route> patch(@PathVariable("id") int id, @RequestBody Route route)
    {
        try
        {
            route = this.routeService.patchRoute(id, route);

            return new ResponseEntity<>(route, HttpStatus.OK);
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

    @Operation(summary = "Delete an Route base on id in path variable")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id)
    {
        try
        {
            routeService.deleteRoute(id);
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