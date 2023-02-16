package vincentcorp.vshop.Authenticator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController 
{
    @Autowired
    private UserService userService;

    @PostMapping
    public User createNewUser(@RequestBody User user)
    {
        user = this.userService.createUser(user);
        return user;
    }

    @PutMapping
    public User modifyUser(@RequestBody User user)
    {
        user = this.userService.modifyUser(user);
        return user;
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable int id)
    {
        this.userService.deleteUser(id);
    }
}
