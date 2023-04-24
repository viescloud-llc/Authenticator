package vincentcorp.vshop.Authenticator.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;

import io.netty.util.internal.ObjectUtil;
import vincentcorp.vshop.Authenticator.dao.UserDao;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.util.HttpResponseThrowers;
import vincentcorp.vshop.Authenticator.util.JwtTokenUtil;

@Service
public class JwtService 
{
    public static final String HASH_KEY = "vincentcorp.vshop.Authenticator.jwt";

    // @Value("${spring.cache.redis.jwtTTL}")
    private int jwtTTL = 1200;

    @Autowired
    private Gson gson;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDao userDao;

    public void logout(String jwt) {
        try
        {
            if(jwt.toLowerCase().contains("bearer"))
                jwt = jwt.split(" ")[1];

            String key = String.format("%s.%s", HASH_KEY, jwt);

            this.redisTemplate.opsForValue().getAndDelete(key);
        }
        catch(Exception ex)
        {
            HttpResponseThrowers.throwServerError("Redis is down?");
        }
    }

    public boolean isJwtExist(String jwt) {
        try
        {
            if(jwt.toLowerCase().contains("bearer"))
                jwt = jwt.split(" ")[1];

            this.validateTokenExpiration(jwt);

            return true;
        }
        catch(ResponseStatusException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            return (boolean) HttpResponseThrowers.throwServerError("Redis is down?");
        }
    }

    public String generateJwtToken(User user)
    {
        try
        {
            String jwt = this.jwtTokenUtil.generateToken(user);

            String key = String.format("%s.%s", HASH_KEY, jwt);

            this.redisTemplate.opsForValue().setIfAbsent(key, gson.toJson(User.builder().id(user.getId()).build()), Duration.ofSeconds(jwtTTL));

            return jwt;
        }
        catch(Exception ex)
        {
            return (String) HttpResponseThrowers.throwServerError("Server can't store JWT");
        }
    }
    
    public User getUser(String jwt)
    {
        if(jwt.toLowerCase().contains("bearer"))
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
        boolean isExpired;
        try
        {
            // isExpired = this.jwtTokenUtil.isTokenExpired(jwt);
            String key = String.format("%s.%s", HASH_KEY, jwt);

            String object = this.redisTemplate.opsForValue().getAndExpire(key, Duration.ofSeconds(jwtTTL));

            isExpired = object == null;
        }
        catch(Exception ex)
        {
            isExpired = true;
        }

        if(isExpired)
            HttpResponseThrowers.throwUnauthorized("JWT token is invalid or expired");
    }
}
