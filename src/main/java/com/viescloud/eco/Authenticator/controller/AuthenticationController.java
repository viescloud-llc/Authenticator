package com.viescloud.eco.Authenticator.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.viescloud.eco.Authenticator.model.Jwt;
import com.viescloud.eco.Authenticator.model.User;
import com.viescloud.eco.Authenticator.model.openId.OpenIdRequest;
import com.viescloud.eco.Authenticator.service.JwtService;
import com.viescloud.eco.Authenticator.service.OpenIdService;
import com.viescloud.eco.Authenticator.service.UserService;
import com.viescloud.eco.viesspringutils.exception.HttpResponseThrowers;
import com.viescloud.eco.viesspringutils.util.ReflectionUtils;


@RestController
@RequestMapping("auth")
public class AuthenticationController 
{
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private OpenIdService openIdService;

    @GetMapping
    public ResponseEntity<?> isLogin(@RequestHeader("Authorization") String jwt) {
        var valid = this.jwtService.tryCheckIsJwtExist(jwt);
        if(valid)
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @GetMapping("/logout")
    public void logout(@RequestHeader("Authorization") String jwt) {
        this.jwtService.logout(jwt);
    }

    @PostMapping("/openId")
    public ResponseEntity<Jwt> postMethodName(@RequestBody OpenIdRequest openIdRequest) {
        var userInfo = openIdService.getUserInfo(openIdRequest);
        var user = this.userService.loginWithOpenId(userInfo);
        return getJwtResponse(user);
    }
    
    @PostMapping(value = "/login", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Jwt> login(@RequestBody User user)
    {
        if (user.getUsername() == null || user.getPassword() == null)
            return HttpResponseThrowers.throwBadRequest("Missing/invalid username or password");

        user = ReflectionUtils.encodingObject(user);
        user = this.userService.login(user);
        return getJwtResponse(user);
    }

    private ResponseEntity<Jwt> getJwtResponse(User user) {
        String jwt = this.jwtService.generateJwtToken(user);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("Authorization", String.format("Bearer %s", jwt));

        return new ResponseEntity<Jwt>(new Jwt(jwt), map, 201);
    }

    @PostMapping("/any_authority")
    public ResponseEntity<String> hasAnyAuthority(@RequestHeader("Authorization") String jwt, @RequestBody List<String> roles)
    {
        User user = this.jwtService.getUser(jwt);
        boolean hasAuthority = this.userService.hasAnyAuthority(user, roles);
        String body = String.format("{\"hasAuthority\":%s}", hasAuthority);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @PostMapping("/all_authority")
    public ResponseEntity<String> hasAllAuthority(@RequestHeader("Authorization") String jwt, @RequestBody List<String> roles)
    {
        User user = this.jwtService.getUser(jwt);
        boolean hasAuthority = this.userService.hasAllAuthority(user, roles);
        String body = String.format("{\"hasAuthority\":%s}", hasAuthority);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/token/api/temporary")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> generateApiToken(@RequestHeader(required = false, value = "Authorization") String jwt1, @RequestBody(required = false) String jwt2) {
        if(jwt1 != null && !jwt1.isEmpty() && !jwt1.isBlank()) {
            var user = this.jwtService.getUser(jwt1);
            var token = this.jwtService.generateToken(user);
            return Map.of("token", token);
        }

        if(jwt2 != null && !jwt2.isEmpty() && !jwt2.isBlank()) {
            var user = this.jwtService.getUser(jwt2);
            var token = this.jwtService.generateToken(user);
            return Map.of("token", token);
        }

        return (Map<String, String>) HttpResponseThrowers.throwUnauthorized("Invalid or missing jwt token");
    }

    @PutMapping("/user")
    public User modifyUser(@RequestHeader("Authorization") String jwt, @RequestBody User user) {
        user = ReflectionUtils.encodingObject(user);
        User jUser = this.jwtService.getUser(jwt);
        var response = this.userService.put(jUser.getId(), user);
        return ReflectionUtils.decodingObject(response);
    }

    @PatchMapping("/user")
    public User patchUser(@RequestHeader("Authorization") String jwt, @RequestBody User user) {
        user = ReflectionUtils.encodingObject(user);
        User jUser = this.jwtService.getUser(jwt);
        var response = this.userService.patch(jUser.getId(), user);
        return ReflectionUtils.decodingObject(response);
    }
}
