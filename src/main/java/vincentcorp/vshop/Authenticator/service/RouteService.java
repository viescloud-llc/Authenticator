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

import vincentcorp.vshop.Authenticator.dao.RouteDao;
import vincentcorp.vshop.Authenticator.model.Route;
import vincentcorp.vshop.Authenticator.util.ReflectionUtils;
import vincentcorp.vshop.Authenticator.util.splunk.Splunk;

@Service
public class RouteService
{
    public static final String HASH_KEY = "vincentcorp.vshop.Authenticator.routes";

    // @Value("${spring.cache.redis.routeTTL}")
    private int routeTTL = 600;

    @Autowired
    private Gson gson;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RouteDao routeDao;

    public List<Route> getAll()
    {
        return this.routeDao.findAll();
    }

    public Route getById(int id)
    {
        //get from redis
        String key = String.format("%s.%s", HASH_KEY, id);
        try
        {
            String jsonRoute = this.redisTemplate.opsForValue().get(key);
            if(jsonRoute != null)
                return this.gson.fromJson(jsonRoute, Route.class);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        //get from database
        Optional<Route> oRoute = this.routeDao.findById(id);

        if(oRoute.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Route ID not found");

        Route route = oRoute.get();

        //save to redis
        try
        {
            this.redisTemplate.opsForValue().set(key, gson.toJson(route));
            this.redisTemplate.expire(key, routeTTL, TimeUnit.SECONDS);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        return route;
    }

    public List<Route> getAllByMatchAll(Route route)
    {
        Example<Route> example = (Example<Route>) ReflectionUtils.getMatchAllMatcher(route);
        return this.routeDao.findAll(example);
    }

    public List<Route> getAllByMatchAny(Route route)
    {
        Example<Route> example = (Example<Route>) ReflectionUtils.getMatchAnyMatcher(route);
        return this.routeDao.findAll(example);
    }

    public Route createRoute(Route route)
    {
        route = this.routeDao.save(route);
        return route;
    }

    public Route modifyRoute(int id, Route route)
    {
        Route oldRoute = this.getById(id);

		ReflectionUtils.replaceValue(oldRoute, route);

        oldRoute = this.routeDao.save(oldRoute);

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

        return oldRoute;
    }

    public List<Route> createDefaultRoute()
    {
        List<Route> routes = this.getAll();
        List<Route> defaultRoutes = new ArrayList<>();
        defaultRoutes.add(Route.builder().method("POST").path("/authenticator/auth/login").secure(false).build());
        defaultRoutes.add(Route.builder().method("POST").path("/authenticator/users").secure(false).build());

        defaultRoutes.stream().forEach(e1 -> {
            if(!routes.parallelStream().anyMatch(e2 -> e2.getMethod().equals(e1.getMethod()) && e2.getPath().equals(e1.getPath())))
            {
                this.routeDao.save(e1);
            }
        });
        
        return this.getAll();
    }

    public Route patchRoute(int id, Route route)
    {
        Route oldRoute = this.getById(id);

		ReflectionUtils.patchValue(oldRoute, route);

        oldRoute = this.routeDao.save(oldRoute);

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

        return oldRoute;
    }

    public void deleteRoute(int id)
    {
        this.routeDao.deleteById(id);

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