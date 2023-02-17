package vincentcorp.vshop.Authenticator.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Multimap;

import io.micrometer.common.util.StringUtils;
import vincentcorp.vshop.Authenticator.http.HttpResponseThrowers;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.service.JwtService;
import vincentcorp.vshop.Authenticator.service.UserService;

@RestController
@RequestMapping("auth")
public class AuthenticationController 
{
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user)
    {
        user = this.userService.login(user);
        String jwt = this.jwtService.generateJwtToken(user);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("Authorization", String.format("Bearer %s", jwt));
        return new ResponseEntity<String>(jwt, map, 201);
    }

    @PostMapping("/any_authority")
    public boolean hasAnyAuthority(@RequestHeader("Authorization") String jwt, @RequestBody List<String> roles)
    {
        User user = this.jwtService.getUser(jwt);
        return this.userService.hasAnyAuthority(user, roles);
    }

    @PostMapping("/all_authority")
    public boolean hasAllAuthority(@RequestHeader("Authorization") String jwt, @RequestBody List<String> roles)
    {
        User user = this.jwtService.getUser(jwt);
        return this.userService.hasAllAuthority(user, roles);
    }
}
