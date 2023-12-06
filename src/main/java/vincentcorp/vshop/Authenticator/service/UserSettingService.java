package vincentcorp.vshop.Authenticator.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.vincent.inc.viesspringutils.exception.HttpResponseThrowers;
import com.vincent.inc.viesspringutils.util.DatabaseUtils;
import com.vincent.inc.viesspringutils.util.ReflectionUtils;

import org.springframework.data.domain.Example;
import vincentcorp.vshop.Authenticator.dao.UserSettingDao;
import vincentcorp.vshop.Authenticator.model.UserSetting;

@Service
public class UserSettingService {
    public static final String HASH_KEY = "vincentcorp.vshop.Authenticator.service.UserSettingService";

    private DatabaseUtils<UserSetting, Integer> databaseUtils;

    private UserSettingDao userSettingDao;

    public UserSettingService(DatabaseUtils<UserSetting, Integer> databaseUtils, UserSettingDao userSettingDao) {
        this.databaseUtils = databaseUtils.init(userSettingDao, HASH_KEY);
        this.userSettingDao = userSettingDao;
    }

    public List<UserSetting> getAll() {
        return this.userSettingDao.findAll();
    }

    public UserSetting getById(int id) {
        UserSetting userSetting = this.databaseUtils.getAndExpire(id);

        if (ObjectUtils.isEmpty(userSetting))
            HttpResponseThrowers.throwBadRequest("UserSetting Id not found");

        return userSetting;
    }

    public UserSetting tryGetById(int id) {
        UserSetting userSetting = this.databaseUtils.getAndExpire(id);
        return userSetting;
    }

    public List<UserSetting> getAllByMatchAll(UserSetting userSetting) {
        Example<UserSetting> example = ReflectionUtils.getMatchAllMatcher(userSetting);
        return this.userSettingDao.findAll(example);
    }

    public List<UserSetting> getAllByMatchAny(UserSetting userSetting) {
        Example<UserSetting> example = ReflectionUtils.getMatchAnyMatcher(userSetting);
        return this.userSettingDao.findAll(example);
    }

    public List<UserSetting> getAllByMatchAll(UserSetting userSetting, String matchCase) {
        Example<UserSetting> example = ReflectionUtils.getMatchAllMatcher(userSetting, matchCase);
        return this.userSettingDao.findAll(example);
    }

    public List<UserSetting> getAllByMatchAny(UserSetting userSetting, String matchCase) {
        Example<UserSetting> example = ReflectionUtils.getMatchAnyMatcher(userSetting, matchCase);
        return this.userSettingDao.findAll(example);
    }

    public UserSetting createUserSetting(UserSetting userSetting) {
        this.databaseUtils.saveAndExpire(userSetting);
        return userSetting;
    }

    public UserSetting modifyUserSetting(int id, UserSetting userSetting) {
        UserSetting oldUserSetting = this.getById(id);

        ReflectionUtils.replaceValue(oldUserSetting, userSetting);

        oldUserSetting = this.databaseUtils.saveAndExpire(oldUserSetting);

        return oldUserSetting;
    }

    public UserSetting patchUserSetting(int id, UserSetting userSetting) {
        UserSetting oldUserSetting = this.getById(id);

        ReflectionUtils.patchValue(oldUserSetting, userSetting);

        oldUserSetting = this.databaseUtils.saveAndExpire(oldUserSetting);

        return oldUserSetting;
    }

    public void deleteUserSetting(int id) {
        this.databaseUtils.deleteById(id);
    }
}