package vincentcorp.vshop.Authenticator.controller;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.model.UserSetting;
import vincentcorp.vshop.Authenticator.service.JwtService;
import vincentcorp.vshop.Authenticator.service.UserSettingService;

@RestController
@RequestMapping("/userSettings")
public class UserSettingController {

    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private JwtService jwtService;

    @Operation(summary = "Get a list of all UserSetting")
    @GetMapping()
    public ResponseEntity<String> getAll(@RequestHeader("Authorization") String jwt) {

        User user = this.jwtService.getUser(jwt);
        var data = this.userSettingService.tryGetById(user.getId());
        
        if (ObjectUtils.isEmpty(data) || ObjectUtils.isEmpty(data.getData()))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(data.getData(), HttpStatus.OK);
    }

    @Operation(summary = "Modify a UserSetting base on jwt")
    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> update(@RequestHeader("Authorization") String jwt, @RequestBody String userSetting) {
        
        User user = this.jwtService.getUser(jwt);
        var data = this.userSettingService.tryGetById(user.getId());
        
        if(ObjectUtils.isEmpty(data)) {
            data = new UserSetting(user.getId(), userSetting);
            this.userSettingService.post(data);
            return new ResponseEntity<>(data.getData(), HttpStatus.CREATED);
        }
        
        data.setData(userSetting);
        data = this.userSettingService.patch(user.getId(), data);
        return new ResponseEntity<>(data.getData(), HttpStatus.OK);
    }
}