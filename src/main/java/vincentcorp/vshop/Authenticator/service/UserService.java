package vincentcorp.vshop.Authenticator.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import vincentcorp.vshop.Authenticator.dao.UserDao;
import vincentcorp.vshop.Authenticator.http.HttpResponseThrowers;
import vincentcorp.vshop.Authenticator.model.User;

@Service
public class UserService 
{
    @Autowired
    private UserDao userDao;

    public List<User> getAll()
    {
        return this.userDao.findAll();
    }

    public User tryGetById(int id)
    {
        Optional<User> user = this.userDao.findById(id);
        return user.isPresent()? user.get() : null;
    }

    public User getById(int id)
    {
        Optional<User> user = this.userDao.findById(id);
        
        return user.isPresent() ? (User) HttpResponseThrowers.throwBadRequest("user ID not found") : null;
    }

    
}
