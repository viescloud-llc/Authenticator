package com.viescloud.eco.Authenticator.schedule;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ObjectUtils;

import com.viescloud.eco.Authenticator.model.User;
import com.viescloud.eco.Authenticator.service.UserService;
import com.viescloud.eco.viesspringutils.util.Booleans;
import com.viescloud.eco.viesspringutils.util.DateTime;
import com.viescloud.eco.viesspringutils.util.MultiTask;

@Configuration
@EnableScheduling
public class UserExpireSchedule {
    private final long DELAY = 24 * 60 * 60 * 1000; //1 day
    private final long INITIAL_DELAY = 60000; //60s
    private final MultiTask<Integer> multiTask = MultiTask.of(0).maxThread(10); 

    @Autowired
    private UserService userService;

    @Scheduled(fixedDelay = DELAY, initialDelay = INITIAL_DELAY)
    public void checkLockUser() {
        long maxId = this.userService.getMaxId();
        
        for (int i = 1; i <= maxId; i++) {
            var id = i;
            this.multiTask.submitTask(() -> this.checkUser(id));
        }
    }

    private void checkUser(int id) {
        userService.getByIdOptional((long) id).ifPresent(user -> {
            isUserExpire(user, userService);
        });
    }

    public static boolean isUserExpire(User user, UserService userService) {
        if (user == null) {
            return true;
        }

        DateTime now = DateTime.now();
        boolean isExpire = false;
        AtomicBoolean isChange = new AtomicBoolean(false);
            
        if(Booleans.isTrue(user.getExpirable()) && !ObjectUtils.isEmpty(user.getExpireTime()) && user.getExpireTime().isBefore(now)) {
            user.setEnable(false);
            user.setExpireTime(null);
            user.setExpirable(false);
            isExpire = true;
            isChange.set(true);
        }

        if(!ObjectUtils.isEmpty(user.getUserApis())) {
            user.getUserApis().forEach(api -> {
                if(Booleans.isTrue(api.getExpirable()) && !ObjectUtils.isEmpty(api.getExpireTime())  && api.getExpireTime().isBefore(now)) {
                    api.setEnable(false);
                    api.setExpireTime(null);
                    api.setExpirable(false);
                    isChange.set(true);
                }
            });
        }

        if (isChange.get()) {
            userService.patch(user.getId(), user);
        }

        return isExpire;
    }
}
