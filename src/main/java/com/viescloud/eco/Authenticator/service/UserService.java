package com.viescloud.eco.Authenticator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.viescloud.eco.Authenticator.dao.RoleDao;
import com.viescloud.eco.Authenticator.dao.UserDao;
import com.viescloud.eco.Authenticator.model.Role;
import com.viescloud.eco.Authenticator.model.User;
import com.viescloud.eco.Authenticator.model.openId.OpenIdUserInfoResponse;
import com.viescloud.eco.Authenticator.schedule.UserExpireSchedule;
import com.viescloud.llc.viesspringutils.exception.HttpResponseThrowers;
import com.viescloud.llc.viesspringutils.repository.DatabaseCall;
import com.viescloud.llc.viesspringutils.service.ViesService;
import com.viescloud.llc.viesspringutils.util.Sha256PasswordEncoder;

import io.micrometer.common.util.StringUtils;

@Service
public class UserService extends ViesService<Long, User, UserDao>
{
    public static final String NORMAL = "NORMAL";

    @Autowired
    private RoleDao roleDao;

    public UserService(DatabaseCall<Long, User> databaseUtils, UserDao repositoryDao) {
        super(databaseUtils, repositoryDao);
    }

    public long getMaxId()
    {
        long maxId = 0;
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

    private void isUsernameExist(Long id, User user) {
        User oldUser = this.getById(id);

        if(!oldUser.getUsername().equals(user.getUsername()) && this.isUsernameExist(user.getUsername()))
            HttpResponseThrowers.throwBadRequest("Username already exist");
    }

    public User loginWithOpenId(OpenIdUserInfoResponse openIdUserInfoResponse) {
        String sub = openIdUserInfoResponse.getSub();
        String email = openIdUserInfoResponse.getEmail();
        String name = openIdUserInfoResponse.getName();

        var foundUser = this.repositoryDao.findBySub(sub);
        
        if(ObjectUtils.isEmpty(foundUser))
            foundUser = this.repositoryDao.findByEmail(email);
        else {
            if(ObjectUtils.isEmpty(foundUser.getEmail())) {
                foundUser.setEmail(email);
                foundUser = this.databaseCall.saveAndExpire(foundUser);
            }
            return updateName(name, foundUser);
        }
        
        if(ObjectUtils.isEmpty(foundUser)) {
            User user = new User();
            user.setEmail(email);
            user.setSub(sub);
            user.setName(name);
            user.setUsername(email.substring(0, email.indexOf("@")));
            user.setEnable(true);
            user.setPassword(Sha256PasswordEncoder.encode(UUID.randomUUID().toString()));
            return this.post(user);
        }
        else {
            if(ObjectUtils.isEmpty(foundUser.getSub())) {
                foundUser.setSub(sub);
                foundUser = this.databaseCall.saveAndExpire(foundUser);
            }
            return updateName(name, foundUser);
        }
    }

    private User updateName(String name, User foundUser) {
        if(ObjectUtils.isEmpty(foundUser.getName()) || !foundUser.getName().equals(name)) {
            foundUser.setName(name);
            foundUser = this.databaseCall.saveAndExpire(foundUser);
        }
        return foundUser;
    }

    public User login(User user)
    {
        var dbUser = this.repositoryDao.findAllByUsername(user.getUsername())
                                       .parallelStream()
                                       .filter(e -> {
                                            var matchPassword = e.getUsername().equals(user.getUsername()) && e.getPassword().equals(user.getPassword());
                                            var matchApiKey = e.getUserApis() != null && e.getUserApis().parallelStream().anyMatch(api -> api.getApiKey() != null && api.getApiKey().equals(user.getPassword()));
                                            return matchPassword || matchApiKey;
                                        })
                                       .findFirst()
                                       .orElseThrow(HttpResponseThrowers.throwConflictException("Invalid username or password"));
        
        if(UserExpireSchedule.isUserExpire(dbUser, this)) {
            return (User) HttpResponseThrowers.throwForbidden("User is expire/lock, please contact administration");
        }

        return dbUser;
    }

    @Override
    protected User processingPostInput(User user) {
        if(this.isUsernameExist(user.getUsername()))
            HttpResponseThrowers.throwBadRequest("Username already exist");
        setDefaultUserRole(user);
        return super.processingPostInput(user);
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
    protected User processingPutInput(Long id, User user) {
        isUsernameExist(id, user);
        user.setPassword(null);
        return super.processingPutInput(id, user);
    }

    public User putUser(Long id, User user) {
        isUsernameExist(id, user);
        return super.put(id, user);
    }

    @Override
    protected User processingPatchInput(Long id, User user) {
        isUsernameExist(id, user);
        user.setPassword(null);
        return super.processingPatchInput(id, user);
    }

    public User patchUser(Long id, User user) {
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

    public void deleteUser(long id) {
        this.databaseCall.deleteByKey(id);
    }

    public boolean hasAnyAuthority(User user, List<String> roles)
    {
        return user.getUserRoles().parallelStream().anyMatch((ur) -> roles.parallelStream().anyMatch(r -> ur.getName().equals(r)));
    }
    
    public boolean hasAllAuthority(User user, List<String> roles)
    {
        return roles.stream().allMatch(r -> user.getUserRoles().parallelStream().anyMatch(ur -> ur.getName().equals(r)));
    }

    @Override
    protected User newEmptyObject() {
        return new User();
    }

    @Override
    public Long getIdFieldValue(User object) {
        return object.getId();
    }
}
