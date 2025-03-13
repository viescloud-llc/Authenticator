package vincentcorp.vshop.Authenticator.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;
import com.viescloud.llc.viesspringutils.exception.HttpResponseThrowers;
import com.viescloud.llc.viesspringutils.repository.DatabaseCall;
import com.viescloud.llc.viesspringutils.util.DateTime;
import com.viescloud.llc.viesspringutils.util.Sha256PasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import vincentcorp.vshop.Authenticator.dao.UserDao;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.util.JwtTokenUtil;

@Service
@Slf4j
public class JwtService {
    public static final String HASH_JWT_KEY = "vincentcorp.vshop.Authenticator.JwtService.jwt";
    public static final String HASH_TOKEN_KEY = "vincentcorp.vshop.Authenticator.JwtService.token";

    private Gson gson = new Gson();

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDao userDao;

    private DatabaseCall<String, String> tokenCache;
    private DatabaseCall<String, String> jwtCache;

    public JwtService(DatabaseCall<String, String> tokenCache, DatabaseCall<String, String> jwtCache) {
        this.tokenCache = tokenCache;
        tokenCache.hashes(HASH_TOKEN_KEY).ttl(DateTime.ofSeconds(30));

        this.jwtCache = jwtCache;
        jwtCache.hashes(HASH_JWT_KEY).ttl(DateTime.ofSeconds(1200));
    }

    public void logout(String jwt) {
        if (jwt.toLowerCase().contains("bearer"))
            jwt = jwt.split(" ")[1];

        this.jwtCache.delete(jwt);
    }

    public boolean tryCheckIsJwtExist(String jwt) {
        try {
            return this.isJwtExist(jwt);
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isJwtExist(String jwt) {
        try {
            if (jwt.toLowerCase().contains("bearer"))
                jwt = jwt.split(" ")[1];

            this.validateJwtExpiration(jwt);

            return true;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            return (boolean) HttpResponseThrowers.throwServerError("Redis is down?");
        }
    }

    public String generateJwtToken(User user) {
        String jwt = this.jwtTokenUtil.generateToken(user);
        User tempUser = new User();
        tempUser.setId(user.getId());
        this.generateFrom(jwt, gson.toJson(tempUser), jwtCache, "can't store JWT in redis");
        return jwt;
    }

    public String generateToken(User user) {
        var uuid = java.util.UUID.randomUUID().toString();
        String token = Sha256PasswordEncoder.encode(uuid);
        User tempUser = new User();
        tempUser.setId(user.getId());
        this.generateFrom(token, gson.toJson(tempUser), tokenCache, "can't store token in redis");
        return token;
    }

    private String generateFrom(String key, String value, DatabaseCall<String, String> cache, String errorMessage) {
        var savedValue = cache.saveAndExpire(key, value);
        if(savedValue == null)
            HttpResponseThrowers.throwServerError(errorMessage);

        return key;
    }

    public User getUser(String jwt) {
        String type = null;

        if (jwt.toLowerCase().contains("bearer")) {
            type = "Bearer";
            jwt = jwt.split(" ")[1];
            this.validateJwtExpiration(jwt);
        } else if (jwt.toLowerCase().contains("token")) {
            type = "Token";
            jwt = jwt.split(" ")[1];
            this.validateTokenExpiration(jwt);
        }
        else {
            return (User) HttpResponseThrowers.throwUnauthorized("Invalid Token Type");
        }

        if (type.equals("Bearer")) {
            String jwtUsername = this.jwtTokenUtil.getUsernameFromToken(jwt);
            String jwtPwd = this.jwtTokenUtil.getPwdFromToken(jwt);
            long userId = this.getUserFromJwt(jwt).getId();
            Optional<User> oUser = userDao.findById(userId);

            if (!oUser.isPresent())
                HttpResponseThrowers.throwUnauthorized("user in token is invalid");

            User user = oUser.get();

            if (!jwtPwd.equals(user.getPassword()) || !jwtUsername.equals(user.getUsername()))
                HttpResponseThrowers.throwUnauthorized("Invalid Token");

            return user;
        } else if (type.equals("Token")) {
            long userId = this.getUserFromToken(jwt).getId();
            Optional<User> oUser = userDao.findById(userId);

            if (!oUser.isPresent())
                HttpResponseThrowers.throwUnauthorized("user in token is invalid");

            return oUser.get();
        }

        return (User) HttpResponseThrowers.throwUnauthorized("Invalid Token Type");
    }

    public void validateJwtExpiration(String jwt) {
        this.validateExpiration(jwt, jwtCache, "JWT is invalid or expired");
    }

    public void validateTokenExpiration(String token) {
        this.validateExpiration(token, tokenCache, "token is invalid or expired");
    }

    public void validateExpiration(String key, DatabaseCall<String, String> cache, String errorMessage) {
        boolean isExpired;

        try {
            String object = cache.getAndExpire(key);
            isExpired = object == null;
        } catch (Exception ex) {
            isExpired = true;
        }

        if (isExpired)
            HttpResponseThrowers.throwUnauthorized(errorMessage);
    }

    private User getUserFromJwt(String jwt) {
        return this.getUserFrom(jwt, jwtCache);
    }

    private User getUserFromToken(String token) {
        return this.getUserFrom(token, tokenCache);
    }

    private User getUserFrom(String key, DatabaseCall<String, String> cache) {
        try {
            String object = cache.getAndExpire(key);
            var user = gson.fromJson(object, User.class);
            return this.userDao.findById(user.getId())
                    .orElseThrow(HttpResponseThrowers.throwServerErrorException("can't get user from api token"));
        }
        catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return (User) HttpResponseThrowers.throwServerError("can't get user from redis cache");
        }
        
    }
}