package vincentcorp.vshop.Authenticator.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;
import com.vincent.inc.viesspringutils.exception.HttpResponseThrowers;
import com.vincent.inc.viesspringutils.util.Sha256PasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import vincentcorp.vshop.Authenticator.dao.UserDao;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.util.JwtTokenUtil;

@Service
@Slf4j
public class JwtService 
{
    public static final String HASH_KEY = "vincentcorp.vshop.Authenticator.jwt";

    // @Value("${spring.cache.redis.jwtTTL}")
    private int jwtTTL = 1200;
    private int tokenTTL = 30;

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

    public boolean tryCheckIsJwtExist(String jwt) {
        try
        {
            if(jwt.toLowerCase().contains("bearer"))
                jwt = jwt.split(" ")[1];

            this.validateTokenExpiration(jwt);

            return true;
        }
        catch(Exception ex)
        {
            return false;
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

            User tempUser = new User();
            tempUser.setId(user.getId());

            this.redisTemplate.opsForValue().setIfAbsent(key, gson.toJson(tempUser), Duration.ofSeconds(jwtTTL));

            return jwt;
        }
        catch(Exception ex)
        {
            return (String) HttpResponseThrowers.throwServerError("Server can't store JWT");
        }
    }

    public String generateApiToken(User user) {
        return this.generateApiToken(user, this.tokenTTL);
    }

    public String generateApiToken(User user, int ttl) {
        try {
            var uuid = java.util.UUID.randomUUID().toString();
            String token = Sha256PasswordEncoder.encode(uuid);
            String key = String.format("%s.%s", HASH_KEY, token);
            User tempUser = new User();
            tempUser.setId(user.getId());
            this.redisTemplate.opsForValue().setIfAbsent(key, gson.toJson(tempUser), Duration.ofSeconds(ttl));
            return token;
        }
        catch(Exception ex) {
            return (String) HttpResponseThrowers.throwServerError("Server can't store API Token");
        }
    }
    
    public User getUser(String jwt)
    {
        String type = null;

        if(jwt.toLowerCase().contains("bearer")) {
            type = "Bearer";
            jwt = jwt.split(" ")[1];
            this.validateTokenExpiration(jwt);
        }
        else if(jwt.toLowerCase().contains("token")) {
            type = "Token";
            jwt = jwt.split(" ")[1];
            this.validateTokenExpiration(jwt, 30);
        }
            
        if(type.equals("Bearer")) {
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
                HttpResponseThrowers.throwUnauthorized("user in token is invalid");

            String tokenPwd = this.jwtTokenUtil.getPwdFromToken(jwt);

            User user = oUser.get();

            if(!tokenPwd.equals(user.getPassword()))
                HttpResponseThrowers.throwUnauthorized("Invalid Token");

            return user;
        } 
        else if(type.equals("Token")) {
            return this.getUserFromApiToken(jwt);
        }

        return (User) HttpResponseThrowers.throwUnauthorized("Invalid Token");
    }

    public void validateTokenExpiration(String jwt) {
        this.validateTokenExpiration(jwt, jwtTTL);
    }

    public void validateTokenExpiration(String jwt, int ttl) {
        boolean isExpired;
        try
        {
            // isExpired = this.jwtTokenUtil.isTokenExpired(jwt);
            String key = String.format("%s.%s", HASH_KEY, jwt);

            String object = this.redisTemplate.opsForValue().getAndExpire(key, Duration.ofSeconds(ttl));

            isExpired = object == null;
        }
        catch(Exception ex)
        {
            isExpired = true;
        }

        if(isExpired)
            HttpResponseThrowers.throwUnauthorized("JWT token is invalid or expired");
    }

    private User getUserFromApiToken(String token) {
        try {
            String key = String.format("%s.%s", HASH_KEY, token);
            String object = this.redisTemplate.opsForValue().get(key);
            User user = gson.fromJson(object, User.class);
            return this.userDao.findById(user.getId()).orElseThrow(() -> HttpResponseThrowers.throwServerErrorException("can't get user from api token"));
        }
        catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return (User) HttpResponseThrowers.throwServerError("can't get user from api token");
        }
    }
}
