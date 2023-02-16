package vincentcorp.vshop.Authenticator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ctc.wstx.util.StringUtil;

import vincentcorp.vshop.Authenticator.dao.RoleDao;
import vincentcorp.vshop.Authenticator.dao.UserDao;
import vincentcorp.vshop.Authenticator.http.HttpResponseThrowers;
import vincentcorp.vshop.Authenticator.model.Role;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.model.UserRole;
import vincentcorp.vshop.Authenticator.util.Constants;
import vincentcorp.vshop.Authenticator.util.ReplacementUtils;
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

    public User createUser(User user)
    {
        user.setPassword(sha256PasswordEncoder.encode(user.getPassword()));
        List<UserRole> roles = new ArrayList<>();
        Role role = roleDao.findByName(Constants.NORMAL);

        if(role != null)
            roles.add(new UserRole(role));

        user.setUserRoles(roles);
        user = this.userDao.save(user);
        return user;
    }

    public User modifyUser(User user)
    {
        User oldUser = this.getById(user.getId());

        String newPassword = user.getPassword();
        
        user.setPassword(null);
        
        ReplacementUtils.replaceValue(oldUser, user);

        if(!StringUtils.isEmpty(newPassword) && !oldUser.getPassword().equals(newPassword))
        {
            newPassword = sha256PasswordEncoder.encode(newPassword);
            if(!oldUser.getPassword().equals(newPassword))
                oldUser.setPassword(sha256PasswordEncoder.encode(newPassword));
        }

        oldUser = userDao.save(oldUser);

        return oldUser;
    }

    public void deleteUser(int id)
    {
        try{this.userDao.deleteById(id);}catch(Exception ex) {}
    }
}
