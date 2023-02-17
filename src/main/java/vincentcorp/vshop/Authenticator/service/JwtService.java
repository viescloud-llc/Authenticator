package vincentcorp.vshop.Authenticator.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vincentcorp.vshop.Authenticator.dao.UserDao;
import vincentcorp.vshop.Authenticator.http.HttpResponseThrowers;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.util.JwtTokenUtil;

@Service
public class JwtService 
{
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDao userDao;

    public String generateJwtToken(User user)
    {
        return this.jwtTokenUtil.generateToken(user);
    }

    public User getUser(String jwt)
    {
        if(jwt.contains("Bearer"))
            jwt = jwt.split(" ")[1];
            
        this.validateTokenExpiration(jwt);

        String username = this.jwtTokenUtil.getUsernameFromToken(jwt);
        String pwd = this.jwtTokenUtil.getPwdFromToken(jwt);
        List<User> users = this.userDao.findAllByUsername(username);
        AtomicInteger userID = new AtomicInteger();
        users.parallelStream().forEach(u -> {
            if(u.getUsername().equals(username) && u.getPassword().equals(pwd))
                userID.set(u.getId());
        });

        Optional<User> oUser = userDao.findById(userID.get());

        if(!oUser.isPresent())
            HttpResponseThrowers.throwBadRequest("user in token is invalid");

        String tokenPwd = this.jwtTokenUtil.getPwdFromToken(jwt);

        User user = oUser.get();

        if(!tokenPwd.equals(user.getPassword()))
            HttpResponseThrowers.throwBadRequest("Invalid Token");

        return user;
    }

    public void validateTokenExpiration(String jwt)
    {
        boolean isExpired = this.jwtTokenUtil.isTokenExpired(jwt);
        if(isExpired)
            HttpResponseThrowers.throwBadRequest("JWT token have been expired");
    }
}
