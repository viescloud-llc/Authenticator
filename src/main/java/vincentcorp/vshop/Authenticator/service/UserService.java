package vincentcorp.vshop.Authenticator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.gson.Gson;

import io.micrometer.common.util.StringUtils;
import vincentcorp.vshop.Authenticator.dao.RoleDao;
import vincentcorp.vshop.Authenticator.dao.UserDao;
import vincentcorp.vshop.Authenticator.model.Role;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.util.DatabaseUtils;
import vincentcorp.vshop.Authenticator.util.ReflectionUtils;
import vincentcorp.vshop.Authenticator.util.Sha256PasswordEncoder;
import vincentcorp.vshop.Authenticator.util.Time;
import vincentcorp.vshop.Authenticator.util.Http.HttpResponseThrowers;
import vincentcorp.vshop.Authenticator.util.splunk.Splunk;

@Service
public class UserService 
{
    public static final String NORMAL = "NORMAL";
    
    public static final String HASH_KEY = "vincentcorp.vshop.Authenticator.users";

    private DatabaseUtils<User, Integer> databaseUtils;

    @Autowired
    private Sha256PasswordEncoder sha256PasswordEncoder;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    public UserService(DatabaseUtils<User, Integer> databaseUtils, UserDao userDao) {
        this.databaseUtils = databaseUtils.init(userDao, HASH_KEY);
        this.userDao = userDao;
    }

    public List<User> getAll()
    {
        return this.userDao.findAll();
    }

    public int getMaxId()
    {
        int maxId = 0;
        try {
            maxId = this.userDao.getMaxId();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        
        return maxId;
    }

    public User tryGetById(int id)
    {
        Optional<User> user = this.userDao.findById(id);
        return user.isPresent() ? user.get() : null;
    }

    public User getById(int id) {
        User user = this.databaseUtils.getAndExpire(id);

        if (ObjectUtils.isEmpty(user))
            HttpResponseThrowers.throwBadRequest("User Id not found");

        return user;
    }

    public List<User> getAllByMatchAll(User user) {
        Example<User> example = ReflectionUtils.getMatchAllMatcher(user);
        return this.userDao.findAll(example);
    }

    public List<User> getAllByMatchAny(User user) {
        Example<User> example = ReflectionUtils.getMatchAnyMatcher(user);
        return this.userDao.findAll(example);
    }

    public List<User> getAllByMatchAll(User user, String matchCase) {
        Example<User> example = ReflectionUtils.getMatchAllMatcher(user, matchCase);
        return this.userDao.findAll(example);
    }

    public List<User> getAllByMatchAny(User user, String matchCase) {
        Example<User> example = ReflectionUtils.getMatchAnyMatcher(user, matchCase);
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
        
        if(this.checkUserExpire(nUser)) {
            return (User) HttpResponseThrowers.throwForbidden("User is expire/lock, please contact administration");
        }

        return nUser;
    }

    public User createUser(User user)
    {
        if(this.isUsernameExist(user.getUsername()))
            HttpResponseThrowers.throwBadRequest("Username already exist");

        user.setPassword(sha256PasswordEncoder.encode(user.getPassword()));
        List<Role> roles = new ArrayList<>();
        Role role = roleDao.findByName(NORMAL);

        if(role == null)
        {
            role = new Role();
            role.setLevel(1);
            role.setName(NORMAL);
            role = this.roleDao.save(role);
        }

        roles.add(role);

        user.setUserRoles(roles);
        user = this.databaseUtils.save(user);
        return user;
    }

    public User modifyUser(int id, User user)
    {
        User oldUser = this.getById(id);

        if(!oldUser.getUsername().equals(user.getUsername()) && this.isUsernameExist(user.getUsername()))
            HttpResponseThrowers.throwBadRequest("Username already exist");

        String newPassword = sha256PasswordEncoder.encode(user.getPassword());
        
        ReflectionUtils.replaceValue(oldUser, user);

        oldUser.setPassword(newPassword);

        oldUser = this.databaseUtils.save(oldUser);

        return oldUser;
    }

    public User patchUser(int id, User user)
    {
        User oldUser = this.getById(id);

        if(!oldUser.getUsername().equals(user.getUsername()) && this.isUsernameExist(user.getUsername()))
            HttpResponseThrowers.throwBadRequest("Username already exist");

        String newPassword = user.getPassword();
        
        user.setPassword(null);
        
        ReflectionUtils.patchValue(oldUser, user);

        validatePassword(oldUser, newPassword);

        oldUser = this.databaseUtils.save(oldUser);

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

    public void deleteUser(int id) {
        this.databaseUtils.deleteById(id);
    }

    public boolean hasAnyAuthority(User user, List<String> roles)
    {
        return user.getUserRoles().parallelStream().anyMatch((ur) -> roles.parallelStream().anyMatch(r -> ur.getName().equals(r)));
    }
    
    public boolean hasAllAuthority(User user, List<String> roles)
    {
        return roles.stream().allMatch(r -> user.getUserRoles().parallelStream().anyMatch(ur -> ur.getName().equals(r)));
    }

    public boolean checkUserExpire(User user) {
        Time now = new Time();

        if(user.isExpirable() && !ObjectUtils.isEmpty(user.getExpireTime())  && user.getExpireTime().toTime().isBefore(now)) {
            user.setEnable(false);
            user.setExpireTime(null);
            user.setExpirable(false);
        }

        if(!ObjectUtils.isEmpty(user.getUserApis()))
            user.getUserApis().forEach(api -> {
                if(api.isExpirable() && !ObjectUtils.isEmpty(api.getExpireTime())  && api.getExpireTime().toTime().isBefore(now)) {
                    api.setEnable(false);
                    api.setExpireTime(null);
                    api.setExpirable(false);
                }
            });

        this.userDao.save(user);

        return !user.isEnable();
    }
}
