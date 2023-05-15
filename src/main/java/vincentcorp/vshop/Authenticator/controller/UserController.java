package vincentcorp.vshop.Authenticator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.QueryParam;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.model.response.UsernameExistResponse;
import vincentcorp.vshop.Authenticator.service.JwtService;
import vincentcorp.vshop.Authenticator.service.UserService;
import vincentcorp.vshop.Authenticator.util.Http.HttpResponseThrowers;
import vincentcorp.vshop.Authenticator.util.splunk.Splunk;

@RestController
@RequestMapping("/users")
public class UserController 
{
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;

    @Operation(summary = "Get All Users")
    @GetMapping("all")
    public ResponseEntity<List<User>> getUsers()
    {
        try
        {
            List<User> list = this.userService.getAll();

            return new ResponseEntity<>(list, HttpStatus.OK);
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

    @Operation(summary = "Check if username already exist")
    @GetMapping("/username/{username}")
    public ResponseEntity<UsernameExistResponse> checkValidUsername(@PathVariable("username") String username)
    {
        try
        {
            boolean exist = this.userService.isUsernameExist(username);

            return new ResponseEntity<>(new UsernameExistResponse(exist), HttpStatus.OK);
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

    @Operation(summary = "Get User from JWT token")
    @GetMapping
    public User getUser(@RequestHeader(required = false, value = "Authorization") String jwt1, @RequestBody(required = false) String jwt2)
    {
        if(jwt1 != null && !jwt1.isEmpty() && !jwt1.isBlank())
            return this.jwtService.getUser(jwt1);

        if(jwt2 != null && !jwt2.isEmpty() && !jwt2.isBlank())
            return this.jwtService.getUser(jwt2);

        return (User) HttpResponseThrowers.throwBadRequest("Invalid or missing jwt token");
    }

    @Operation(summary = "Get User base on id in path variable")
    @GetMapping("{id}")
    public ResponseEntity<User> getById(@PathVariable("id") int id)
    {
        try
        {
            User user = userService.getById(id);

            return new ResponseEntity<>(user, HttpStatus.OK);
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

    @Operation(summary = "Get a list of all User that match all information base on query parameter")
    @GetMapping("match_all")
    public ResponseEntity<List<User>> matchAll(@QueryParam("user") User user)
    {
        try
        {
            List<User> users = this.userService.getAllByMatchAll(user);

            if (users.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(users, HttpStatus.OK);
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

    @Operation(summary = "Get a list of all User that match any information base on query parameter")
    @GetMapping("match_any")
    public ResponseEntity<List<User>> matchAny(@QueryParam("user") User user)
    {
        try
        {
            List<User> users = this.userService.getAllByMatchAny(user);

            if (users.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(users, HttpStatus.OK);
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
    
    @Operation(summary = "Create a new User")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> create(@RequestBody User user)
    {
        try
        {
            User savedUser = userService.createUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
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

    @Operation(summary = "Modify an User base on id in path variable")
    @PutMapping("{id}")
    public ResponseEntity<User> update(@PathVariable("id") int id, @RequestBody User user)
    {
        try
        {
            user = this.userService.modifyUser(id, user);

            return new ResponseEntity<>(user, HttpStatus.OK);
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

    @Operation(summary = "Patch an User base on id in path variable")
    @PatchMapping("{id}")
    public ResponseEntity<User> patch(@PathVariable("id") int id, @RequestBody User user)
    {
        try
        {
            user = this.userService.patchUser(id, user);

            return new ResponseEntity<>(user, HttpStatus.OK);
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

    @Operation(summary = "Delete an User base on id in path variable")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id)
    {
        try
        {
            userService.deleteUser(id);
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
