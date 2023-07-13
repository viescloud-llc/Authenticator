package vincentcorp.vshop.Authenticator.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vincentcorp.vshop.Authenticator.model.UserSetting;

public interface UserSettingDao extends JpaRepository<UserSetting, Integer> {
public UserSetting findByData(String data);
	public List<UserSetting> findAllByData(String data);

	@Query(value = "select * from UserSetting as userSetting where userSetting.data = :data", nativeQuery = true)
	public List<UserSetting> getAllByMatchAll(@Param("data") String data);

	@Query(value = "select * from UserSetting as userSetting where userSetting.data = :data", nativeQuery = true)
	public List<UserSetting> getAllByMatchAny(@Param("data") String data);
}