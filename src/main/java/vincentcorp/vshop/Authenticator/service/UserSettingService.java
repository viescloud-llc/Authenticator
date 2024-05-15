package vincentcorp.vshop.Authenticator.service;

import org.springframework.stereotype.Service;
import com.vincent.inc.viesspringutils.service.ViesService;
import com.vincent.inc.viesspringutils.util.DatabaseCall;
import vincentcorp.vshop.Authenticator.dao.UserSettingDao;
import vincentcorp.vshop.Authenticator.model.UserSetting;

@Service
public class UserSettingService extends ViesService<UserSetting, Integer, UserSettingDao> {

    public UserSettingService(DatabaseCall<UserSetting, Integer> databaseCall, UserSettingDao repositoryDao) {
        super(databaseCall, repositoryDao);
    }

    @Override
    protected UserSetting newEmptyObject() {
        return new UserSetting();
    }
    
}