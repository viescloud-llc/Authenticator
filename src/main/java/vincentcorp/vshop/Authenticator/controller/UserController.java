package vincentcorp.vshop.Authenticator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.vincent.inc.viesspringutils.exception.HttpResponseThrowers;
import com.vincent.inc.viesspringutils.interfaces.RemoveHashing;
import com.vincent.inc.viesspringutils.interfaces.InputHashing;

import io.swagger.v3.oas.annotations.Operation;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.model.response.UsernameExistResponse;
import vincentcorp.vshop.Authenticator.service.JwtService;
import vincentcorp.vshop.Authenticator.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController
{
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserService userService;

    @Operation(summary = "Get a list of all User")
    @GetMapping("all")
    @RemoveHashing
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAll();

        if (users.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Check if username already exist")
    @GetMapping("/username/{username}")
    public ResponseEntity<UsernameExistResponse> checkValidUsername(@PathVariable("username") String username)
    {
        boolean exist = this.userService.isUsernameExist(username);
        return new ResponseEntity<>(new UsernameExistResponse(exist), HttpStatus.OK);
    }

    @Operation(summary = "Get User from JWT token")
    @GetMapping
    @RemoveHashing
    public User getUser(@RequestHeader(required = false, value = "Authorization") String jwt1, @RequestBody(required = false) String jwt2)
    {
        if(jwt1 != null && !jwt1.isEmpty() && !jwt1.isBlank())
            return this.jwtService.getUser(jwt1);

        if(jwt2 != null && !jwt2.isEmpty() && !jwt2.isBlank())
            return this.jwtService.getUser(jwt2);

        return (User) HttpResponseThrowers.throwUnauthorized("Invalid or missing jwt token");
    }

    @Operation(summary = "Get User base on id in path variable")
    @GetMapping("{id}")
    @RemoveHashing
    public ResponseEntity<User> getById(@PathVariable("id") int id) {
        User user = userService.getById(id);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Create a new User")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @InputHashing
    @RemoveHashing
    public ResponseEntity<User> post(@RequestBody User user) {
        User savedUser = userService.post(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Modify a User base on id in path variable")
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @InputHashing
    @RemoveHashing
    public ResponseEntity<User> put(@PathVariable("id") int id, @RequestBody User user) {
        user = this.userService.put(id, user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Patch a User base on id in path variable")
    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @InputHashing
    @RemoveHashing
    public ResponseEntity<User> patch(@PathVariable("id") int id, @RequestBody User user) {
        user = this.userService.patch(id, user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Delete a User base on id in path variable")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
