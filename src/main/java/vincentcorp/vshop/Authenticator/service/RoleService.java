package vincentcorp.vshop.Authenticator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.gson.Gson;

import vincentcorp.vshop.Authenticator.dao.RoleDao;
import vincentcorp.vshop.Authenticator.model.Role;
import vincentcorp.vshop.Authenticator.util.ReflectionUtils;
import vincentcorp.vshop.Authenticator.util.splunk.Splunk;

@Service
public class RoleService
{
    public static final String HASH_KEY = "vincentcorp.vshop.Authenticator.roles";

    // @Value("${spring.cache.redis.roleTTL}")
    private int roleTTL = 600;

    @Autowired
    private Gson gson;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RoleDao roleDao;

    public List<Role> getAll()
    {
        return this.roleDao.findAll();
    }

    public Role getById(int id)
    {
        //get from redis
        String key = String.format("%s.%s", HASH_KEY, id);
        try
        {
            String jsonRole = this.redisTemplate.opsForValue().get(key);
            if(jsonRole != null)
                return this.gson.fromJson(jsonRole, Role.class);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        //get from database
        Optional<Role> oRole = this.roleDao.findById(id);

        if(oRole.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role ID not found");

        Role role = oRole.get();

        //save to redis
        try
        {
            this.redisTemplate.opsForValue().set(key, gson.toJson(role));
            this.redisTemplate.expire(key, roleTTL, TimeUnit.SECONDS);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        return role;
    }

    public List<Role> getAllByMatchAll(Role role)
    {
        Example<Role> example = (Example<Role>) ReflectionUtils.getMatchAllMatcher(role);
        return this.roleDao.findAll(example);
    }

    public List<Role> getAllByMatchAny(Role role)
    {
        Example<Role> example = (Example<Role>) ReflectionUtils.getMatchAnyMatcher(role);
        return this.roleDao.findAll(example);
    }

    public Role createRole(Role role)
    {
        role = this.roleDao.save(role);
        return role;
    }

    public Role modifyRole(int id, Role role)
    {
        Role oldRole = this.getById(id);

		oldRole.setName(role.getName());
		oldRole.setLevel(role.getLevel());


        oldRole = this.roleDao.save(oldRole);

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

        return oldRole;
    }

    public List<Role> createDefaultRole() {
        List<Role> roles = this.getAll();
        List<Role> defaultRole = new ArrayList<>();
        defaultRole.add(Role.builder().name("OWNER").level(99).build());
        defaultRole.add(Role.builder().name("CO-OWNER").level(98).build());
        defaultRole.add(Role.builder().name("GUEST").level(0).build());
        defaultRole.add(Role.builder().name("NORMAL").level(1).build());
        defaultRole.add(Role.builder().name("ADMIN").level(5).build());

        defaultRole.stream().forEach(e1 -> {
            if(!roles.parallelStream().anyMatch(e2 -> e2.getName().equals(e1.getName()) && e2.getLevel() == e1.getLevel()))
            {
                this.roleDao.save(e1);
            }
        });
        
        return this.getAll();
    }

    public Role patchRole(int id, Role role)
    {
        Role oldRole = this.getById(id);

		oldRole.setName(role.getName() == null ? oldRole.getName() : role.getName());
		oldRole.setLevel(role.getLevel());


        oldRole = this.roleDao.save(oldRole);

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

        return oldRole;
    }

    public void deleteRole(int id)
    {
        this.roleDao.deleteById(id);

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
}