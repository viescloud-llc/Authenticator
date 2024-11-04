package vincentcorp.vshop.Authenticator.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ObjectUtils;

import com.viescloud.llc.viesspringutils.util.DateTime;

import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.service.UserService;

@Configuration
@EnableScheduling
public class UserExpireSchedule {
    private final long DELAY = 24 * 60 * 60 * 1000; //1 day
    private final long INITIAL_DELAY = 60000; //60s

    @Autowired
    private UserService userService;

    @Scheduled(fixedDelay = DELAY, initialDelay = INITIAL_DELAY)
    public void checkLockUser() {
        int maxId = this.userService.getMaxId();

        var threadPool = Executors.newCachedThreadPool();
        List<Future<User>> futures = new ArrayList<>();
        
        for (int i = 1; i <= maxId; i++) {
            User user = userService.tryGetById(i);

            if(!ObjectUtils.isEmpty(user)) {
                futures.add(threadPool.submit(new CheckUser(user)));
            }
        }

        futures.parallelStream().forEach(f -> {
            while(!f.isDone()) {}

            try {
                User user = f.get();
                this.userService.patch(user.getId(), user);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    class CheckUser implements Callable<User> {
        private User user;

        public CheckUser(User user) {
            this.user = user;
        }

        @Override
        public User call() throws Exception {
            DateTime now = DateTime.now();
            
            if(this.user.isExpirable() && !ObjectUtils.isEmpty(this.user.getExpireTime())  && this.user.getExpireTime().isBefore(now)) {
                this.user.setEnable(false);
                this.user.setExpireTime(null);
                this.user.setExpirable(false);
            }

            if(!ObjectUtils.isEmpty(user.getUserApis()))
                user.getUserApis().forEach(api -> {
                    if(api.isExpirable() && !ObjectUtils.isEmpty(api.getExpireTime())  && api.getExpireTime().isBefore(now)) {
                        api.setEnable(false);
                        api.setExpireTime(null);
                        api.setExpirable(false);
                    }
                });

            return this.user;
        }

    }
}
