package vincentcorp.vshop.Authenticator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import vincentcorp.vshop.Authenticator.dao.RoleDao;
import vincentcorp.vshop.Authenticator.dao.UserDao;
import vincentcorp.vshop.Authenticator.http.HttpResponseThrowers;
import vincentcorp.vshop.Authenticator.model.Role;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.util.Constants;
import vincentcorp.vshop.Authenticator.util.ReflectionUtils;
import vincentcorp.vshop.Authenticator.util.Sha256PasswordEncoder;

@Service
public class UserService 
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private Sha256PasswordEncoder sha256PasswordEncoder;

    @Autowired
    private RoleDao roleDao;

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
        Optional<User> user = this.userDao.findById(id);
        
        return user.isPresent() ? user.get() : (User) HttpResponseThrowers.throwBadRequest("user ID not found");
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

        String newPassword = sha256PasswordEncoder.encode(user.getPassword());
        
        ReflectionUtils.replaceValue(oldUser, user);

        // validatePassword(oldUser, newPassword);

        oldUser.setPassword(newPassword);

        oldUser = userDao.save(oldUser);

        return oldUser;
    }

    public User patchUser(int id, User user)
    {
        User oldUser = this.getById(id);

        String newPassword = user.getPassword();
        
        user.setPassword(null);
        
        ReflectionUtils.patchValue(oldUser, user);

        validatePassword(oldUser, newPassword);

        oldUser = userDao.save(oldUser);

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
        try{this.userDao.deleteById(id);}catch(Exception ex) {}
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
