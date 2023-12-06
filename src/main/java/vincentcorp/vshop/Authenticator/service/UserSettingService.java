package vincentcorp.vshop.Authenticator.service;

import org.springframework.stereotype.Service;
import com.vincent.inc.viesspringutils.service.ViesService;
import com.vincent.inc.viesspringutils.util.DatabaseUtils;
import vincentcorp.vshop.Authenticator.dao.UserSettingDao;
import vincentcorp.vshop.Authenticator.model.UserSetting;

@Service
public class UserSettingService extends ViesService<UserSetting, Integer, UserSettingDao> {

    public UserSettingService(DatabaseUtils<UserSetting, Integer> databaseUtils, UserSettingDao repositoryDao) {
        super(databaseUtils, repositoryDao);
    }

    @Override
    protected UserSetting newEmptyObject() {
        return new UserSetting();
    }
    
}