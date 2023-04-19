package vincentcorp.vshop.Authenticator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.gson.Gson;

import io.micrometer.common.util.StringUtils;
import vincentcorp.vshop.Authenticator.dao.RoleDao;
import vincentcorp.vshop.Authenticator.dao.UserDao;
import vincentcorp.vshop.Authenticator.model.Role;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.util.Constants;
import vincentcorp.vshop.Authenticator.util.HttpResponseThrowers;
import vincentcorp.vshop.Authenticator.util.ReflectionUtils;
import vincentcorp.vshop.Authenticator.util.Sha256PasswordEncoder;
import vincentcorp.vshop.Authenticator.util.splunk.Splunk;

@Service
public class UserService 
{
    public static final String HASH_KEY = "vincentcorp.vshop.Authenticator.users";

    // @Value("${spring.cache.redis.userTTL}")
    private int userTTL = 600;

    @Autowired
    private Gson gson;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private Sha256PasswordEncoder sha256PasswordEncoder;

    public List<User> getAll()
    {
        return this.userDao.findAll();
    }

    public User tryGetById(int id)
    {
        Optional<User> user = this.userDao.findById(id);
        return user.isPresent() ? user.get() : null;
    }

    public User getById(int id)
    {
        //get from redis
        String key = String.format("%s.%s", HASH_KEY, id);
        try
        {
            String jsonUser = this.redisTemplate.opsForValue().get(key);
            if(jsonUser != null)
                return this.gson.fromJson(jsonUser, User.class);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        //get from database
        Optional<User> oUser = this.userDao.findById(id);

        if(oUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID not found");

        User user = oUser.get();

        //save to redis
        try
        {
            this.redisTemplate.opsForValue().set(key, gson.toJson(user));
            this.redisTemplate.expire(key, userTTL, TimeUnit.SECONDS);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        return user;
    }

    public List<User> getAllByMatchAll(User user)
    {
        Example<User> example = (Example<User>) ReflectionUtils.getMatchAllMatcher(user);
        return this.userDao.findAll(example);
    }

    public List<User> getAllByMatchAny(User user)
    {
        Example<User> example = (Example<User>) ReflectionUtils.getMatchAnyMatcher(user);
        return this.userDao.findAll(example);
    }

    /**
     * this method will check if username already exist in database or not
     * @param username username to be check
     * @return return true if exist else false
     */
    public boolean isUsernameExist(String username)
    {   
        List<User> users = userDao.findAllByUsername(username);
        
        return users != null && users.parallelStream().anyMatch(user -> user.getUsername().equals(username));
    }

    public User login(User user)
    {
        user.setPassword(sha256PasswordEncoder.encode(user.getPassword()));
        List<User> users = this.userDao.findAllByUsername(user.getUsername());
        AtomicInteger userID = new AtomicInteger();
        users.parallelStream().forEach(u -> {
            if(u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword()))
                userID.set(u.getId());
        });

        Optional<User> oUser = userDao.findById(userID.get());

        if(oUser.isEmpty())
            HttpResponseThrowers.throwBadRequest("Invalid username or password");

        User nUser = oUser.get();

        return nUser;
    }

    public User createUser(User user)
    {
        if(this.isUsernameExist(user.getUsername()))
            HttpResponseThrowers.throwBadRequest("Username already exist");

        user.setPassword(sha256PasswordEncoder.encode(user.getPassword()));
        List<Role> roles = new ArrayList<>();
        Role role = roleDao.findByName(Constants.NORMAL);

        if(role == null)
        {
            role = new Role();
            role.setLevel(1);
            role.setName(Constants.NORMAL);
            role = this.roleDao.save(role);
        }

        roles.add(role);

        user.setUserRoles(roles);
        user = this.userDao.save(user);
        return user;
    }

    public User modifyUser(int id, User user)
    {
        User oldUser = this.getById(id);

        if(this.isUsernameExist(user.getUsername()))
            HttpResponseThrowers.throwBadRequest("Username already exist");

        String newPassword = sha256PasswordEncoder.encode(user.getPassword());
        
        ReflectionUtils.replaceValue(oldUser, user);

        // validatePassword(oldUser, newPassword);

        oldUser.setPassword(newPassword);

        oldUser = userDao.save(oldUser);

        //remove from redis
        try
        {
            String key = String.format("%s.%s", HASH_KEY, id);
            this.redisTemplate.delete(key);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        return oldUser;
    }

    public User patchUser(int id, User user)
    {
        User oldUser = this.getById(id);

        if(this.isUsernameExist(user.getUsername()))
            HttpResponseThrowers.throwBadRequest("Username already exist");

        String newPassword = user.getPassword();
        
        user.setPassword(null);
        
        ReflectionUtils.patchValue(oldUser, user);

        validatePassword(oldUser, newPassword);

        oldUser = userDao.save(oldUser);

        //remove from redis
        try
        {
            String key = String.format("%s.%s", HASH_KEY, id);
            this.redisTemplate.delete(key);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        return oldUser;
    }

    private void validatePassword(User user, String newPassword) {
        if(!StringUtils.isEmpty(newPassword) && !user.getPassword().equals(newPassword))
        {
            newPassword = sha256PasswordEncoder.encode(newPassword);
            if(!user.getPassword().equals(newPassword))
                user.setPassword(newPassword);
        }
    }

    // private void validateUserRoles(User user)
    // {
    //     List<Role> roles = this.roleDao.findAll();
    //     List<Role> userRoles = user.getUserRoles();
    //     List<Role> newUserRoles = new ArrayList<>();

    //     userRoles.forEach(ur -> {
    //         String roleName = ur.getName();
    //         for(Role role : roles)
    //         {
    //             if(role.getName().equals(roleName))
    //             {
    //                 ur.setRole(role);
    //                 newUserRoles.add(ur);
    //             }
    //         }
    //     });
    //     user.setUserRoles(newUserRoles);
    // }

    public void deleteUser(int id)
    {
        this.userDao.deleteById(id);

        //remove from redis
        try
        {
            String key = String.format("%s.%s", HASH_KEY, id);
            this.redisTemplate.delete(key);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }
    }

    public boolean hasAnyAuthority(User user, List<String> roles)
    {
        return user.getUserRoles().parallelStream().anyMatch((ur) -> roles.parallelStream().anyMatch(r -> ur.getName().equals(r)));
    }
    
    public boolean hasAllAuthority(User user, List<String> roles)
    {
        return roles.stream().allMatch(r -> user.getUserRoles().parallelStream().anyMatch(ur -> ur.getName().equals(r)));
    }
}
