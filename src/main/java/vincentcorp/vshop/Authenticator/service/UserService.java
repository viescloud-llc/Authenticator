package vincentcorp.vshop.Authenticator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.vincent.inc.viesspringutils.exception.HttpResponseThrowers;
import com.vincent.inc.viesspringutils.service.ViesService;
import com.vincent.inc.viesspringutils.util.DatabaseCall;
import com.vincent.inc.viesspringutils.util.DateTime;
import com.vincent.inc.viesspringutils.util.Sha256PasswordEncoder;

import io.micrometer.common.util.StringUtils;
import vincentcorp.vshop.Authenticator.dao.RoleDao;
import vincentcorp.vshop.Authenticator.dao.UserDao;
import vincentcorp.vshop.Authenticator.model.Role;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.model.openId.OpenIdUserInfoResponse;

@Service
public class UserService extends ViesService<User, Integer, UserDao>
{
    public static final String NORMAL = "NORMAL";

    @Autowired
    private RoleDao roleDao;

    public UserService(DatabaseCall<User, Integer> databaseUtils, UserDao repositoryDao) {
        super(databaseUtils, repositoryDao);
    }

    public int getMaxId()
    {
        int maxId = 0;
        try {
            maxId = this.repositoryDao.getMaxId();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        
        return maxId;
    }

    /**
     * this method will check if username already exist in database or not
     * @param username username to be check
     * @return return true if exist else false
     */
    public boolean isUsernameExist(String username)
    {   
        List<User> users = repositoryDao.findAllByUsername(username);
        return users != null && users.parallelStream().anyMatch(user -> user.getUsername().equals(username));
    }

    private void isUsernameExist(Integer id, User user) {
        User oldUser = this.getById(id);

        if(!oldUser.getUsername().equals(user.getUsername()) && this.isUsernameExist(user.getUsername()))
            HttpResponseThrowers.throwBadRequest("Username already exist");
    }

    public User loginWithOpenId(OpenIdUserInfoResponse openIdUserInfoResponse) {
        String sub = openIdUserInfoResponse.getSub();
        String email = openIdUserInfoResponse.getEmail();

        var foundUser = this.repositoryDao.findBySub(sub);
        
        if(ObjectUtils.isEmpty(foundUser))
            foundUser = this.repositoryDao.findByEmail(email);
        else {
            if(ObjectUtils.isEmpty(foundUser.getEmail())) {
                foundUser.setEmail(email);
                foundUser = this.databaseCall.saveAndExpire(foundUser);
            }
            return foundUser;
        }
        
        if(ObjectUtils.isEmpty(foundUser)) {
            User user = new User();
            user.setEmail(email);
            user.setSub(sub);
            user.setName(openIdUserInfoResponse.getName());
            user.setUsername(email.substring(0, email.indexOf("@")));
            user.setEnable(true);
            user.setPassword(UUID.randomUUID().toString());
            return this.post(user);
        }
        else {
            if(ObjectUtils.isEmpty(foundUser.getSub())) {
                foundUser.setSub(sub);
                foundUser = this.databaseCall.saveAndExpire(foundUser);
            }
            return foundUser;
        }
    }

    public User login(User user)
    {
        user.setPassword(Sha256PasswordEncoder.encode(user.getPassword()));
        List<User> users = this.repositoryDao.findAllByUsername(user.getUsername());
        AtomicInteger userID = new AtomicInteger();
        users.parallelStream().forEach(u -> {
            if(u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword()))
                userID.set(u.getId());
        });

        Optional<User> oUser = repositoryDao.findById(userID.get());

        if(oUser.isEmpty())
            HttpResponseThrowers.throwBadRequest("Invalid username or password");

        User nUser = oUser.get();
        
        if(this.checkUserExpire(nUser)) {
            return (User) HttpResponseThrowers.throwForbidden("User is expire/lock, please contact administration");
        }

        return nUser;
    }

    @Override
    public User post(User user) {
        if(this.isUsernameExist(user.getUsername()))
            HttpResponseThrowers.throwBadRequest("Username already exist");
        setDefaultUserRole(user);
        return super.post(user);
    }

    private void setDefaultUserRole(User user) {
        List<Role> roles = new ArrayList<>();
        Role role = roleDao.findByName(NORMAL);

        if(role == null) {
            role = new Role();
            role.setLevel(1);
            role.setName(NORMAL);
            role = this.roleDao.save(role);
        }

        roles.add(role);
        user.setUserRoles(roles);
    }

    @Override
    public User put(Integer id, User user) {
        isUsernameExist(id, user);
        user.setPassword(null);
        return super.put(id, user);
    }

    public User modifyUser(Integer id, User user) {
        isUsernameExist(id, user);
        return super.put(id, user);
    }

    @Override
    public User patch(Integer id, User user) {
        isUsernameExist(id, user);
        user.setPassword(null);
        return super.patch(id, user);
    }

    public User patchUser(Integer id, User user) {
        isUsernameExist(id, user);
        return super.patch(id, user);
    }

    public void validatePassword(User user, String newPassword) {
        if(!StringUtils.isEmpty(newPassword) && !user.getPassword().equals(newPassword))
        {
            newPassword = Sha256PasswordEncoder.encode(newPassword);
            if(!user.getPassword().equals(newPassword))
                user.setPassword(newPassword);
        }
    }

    public void deleteUser(int id) {
        this.databaseCall.deleteById(id);
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
        DateTime now = DateTime.now();

        if(user.isExpirable() && !ObjectUtils.isEmpty(user.getExpireTime())  && user.getExpireTime().toDateTime().isBefore(now)) {
            user.setEnable(false);
            user.setExpireTime(null);
            user.setExpirable(false);
        }

        if(!ObjectUtils.isEmpty(user.getUserApis()))
            user.getUserApis().forEach(api -> {
                if(api.isExpirable() && !ObjectUtils.isEmpty(api.getExpireTime())  && api.getExpireTime().toDateTime().isBefore(now)) {
                    api.setEnable(false);
                    api.setExpireTime(null);
                    api.setExpirable(false);
                }
            });

        this.repositoryDao.save(user);

        return !user.isEnable();
    }

    @Override
    protected User newEmptyObject() {
        return new User();
    }
}
