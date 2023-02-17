package vincentcorp.vshop.Authenticator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.util.JwtTokenUtil;

@SpringBootTest
@Slf4j
public class JwtTest 
{

    @Test
    public void JWTGenerator()
    {
        User user = new User();
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

        user.setUsername("TEST");
        user.setPassword("TEST");
    }
}
