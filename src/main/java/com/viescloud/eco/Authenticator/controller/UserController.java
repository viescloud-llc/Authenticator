package com.viescloud.eco.Authenticator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viescloud.eco.Authenticator.model.User;
import com.viescloud.eco.Authenticator.model.response.UsernameExistResponse;
import com.viescloud.eco.Authenticator.service.JwtService;
import com.viescloud.eco.Authenticator.service.UserService;
import com.viescloud.eco.viesspringutils.controller.ViesController;
import com.viescloud.eco.viesspringutils.exception.HttpResponseThrowers;
import com.viescloud.eco.viesspringutils.model.MatchByEnum;
import com.viescloud.eco.viesspringutils.model.GenericPropertyMatcherEnum.PropertyMatcherEnum;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/users")
public class UserController extends ViesController<Long, User, UserService>
{
    @Autowired
    private JwtService jwtService;

    public UserController(UserService service) {
        super(service);
    }

    @Override
    @GetMapping("all")
    public ResponseEntity<?> getAll(String arg0, Integer arg1, Integer arg2, User arg3, PropertyMatcherEnum arg4,
            MatchByEnum arg5, String arg6) {
        return super.getAll(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }
    
    // @Operation(summary = "Get a list of all User")
    // @GetMapping("all")
    // public ResponseEntity<List<User>> getAll() {
    //     List<User> users = this.service.getAll();

    //     if (users.isEmpty())
    //         return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    //     return new ResponseEntity<>(users, HttpStatus.OK);
    // }

    @Operation(summary = "Check if username already exist")
    @GetMapping("/username/{username}")
    public ResponseEntity<UsernameExistResponse> checkValidUsername(@PathVariable("username") String username)
    {
        boolean exist = this.service.isUsernameExist(username);
        return new ResponseEntity<>(new UsernameExistResponse(exist), HttpStatus.OK);
    }

    @Operation(summary = "Get User from JWT token")
    @GetMapping
    public User getUser(@RequestHeader(required = false, value = "Authorization") String jwt1, @RequestBody(required = false) String jwt2)
    {
        if(jwt1 != null && !jwt1.isEmpty() && !jwt1.isBlank())
            return this.jwtService.getUser(jwt1);

        if(jwt2 != null && !jwt2.isEmpty() && !jwt2.isBlank())
            return this.jwtService.getUser(jwt2);

        return (User) HttpResponseThrowers.throwUnauthorized("Invalid or missing jwt token");
    }
}
