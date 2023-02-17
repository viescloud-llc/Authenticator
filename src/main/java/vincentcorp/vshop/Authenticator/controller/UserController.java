package vincentcorp.vshop.Authenticator.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import vincentcorp.vshop.Authenticator.http.HttpResponseThrowers;
import vincentcorp.vshop.Authenticator.model.User;
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

    @GetMapping
    public User getUser(@RequestHeader(required = false, value = "Authorization") String jwt1, @RequestBody(required = false) String jwt2)
    {
        if(jwt1 != null && !jwt1.isEmpty() && !jwt1.isBlank())
            return this.jwtService.getUser(jwt1);

        if(jwt2 != null && !jwt2.isEmpty() && !jwt2.isBlank())
            return this.jwtService.getUser(jwt2);

        return (User) HttpResponseThrowers.throwBadRequest("Invalid or missing jwt token");
    }
    
    @PostMapping
    public ResponseEntity<User> createNewUser(@RequestBody User user)
    {
        user = this.userService.createUser(user);
        return new ResponseEntity<User>(user, null, 201);
    }
    
    @PutMapping
    public User modifyUser(@RequestBody User user)
    {
        User newUser = this.userService.modifyUser(user);
        return newUser;
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable int id)
    {
        this.userService.deleteUser(id);
    }
}
