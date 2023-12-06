package vincentcorp.vshop.Authenticator.service;

import org.springframework.stereotype.Service;
import com.vincent.inc.viesspringutils.service.ViesService;
import com.vincent.inc.viesspringutils.util.DatabaseUtils;
import vincentcorp.vshop.Authenticator.dao.RouteDao;
import vincentcorp.vshop.Authenticator.model.Route;

@Service
public class RouteService extends ViesService<Route, Integer, RouteDao> {

    public RouteService(DatabaseUtils<Route, Integer> databaseUtils, RouteDao repositoryDao) {
        super(databaseUtils, repositoryDao);
    }

    @Override
    protected Route newEmptyObject() {
        return new Route();
    }
    
}