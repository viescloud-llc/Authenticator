package vincentcorp.vshop.Authenticator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.viescloud.llc.viesspringutils.util.WebCall;

import vincentcorp.vshop.Authenticator.model.Jwt;
import vincentcorp.vshop.Authenticator.model.User;
import vincentcorp.vshop.Authenticator.model.UserProfile;

public class AuthenticationLiveTest {
    private final String baseUrl = "http://localhost:8081";
    private final RestTemplate restTemplate = new RestTemplate();

    // @Test
    public void testPostUser() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        user.setEmail("admin@gmail.com");

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("Admin");
        userProfile.setLastName("Admin");
        userProfile.setAddress("Admin");
        userProfile.setCity("Admin");
        userProfile.setState("Admin");
        userProfile.setZip("Admin");
        userProfile.setPhoneNumber("Admin");
        userProfile.setAlias("Admin");
        user.setUserProfile(userProfile);

        var response = WebCall.of(restTemplate, User.class)
                              .request(HttpMethod.POST, getBaseUrl("users"))
                              .body(user)
                              .exchange()
                              .getResponseBody();

        assertNotNull(response);
        assertTrue(response.getId() > 0);
    }

    // @Test
    public User testGetUser(Long id) {
        String idStr = id == null ? "1" : id + "";
        var response = WebCall.of(restTemplate, User.class)
                              .request(HttpMethod.GET, getBaseUrl("users", idStr))
                              .exchange()
                              .getResponseBody();

        assertNotNull(response);
        assertTrue(response.getId() > 0);
        return response;
    }

    // @Test
    public void testLogin(String username, String password) {
        User user = new User();
        user.setUsername(username == null ? "admin" : username);
        user.setPassword(password == null ? "admin" : password);

        var response = WebCall.of(restTemplate, Jwt.class)
                              .request(HttpMethod.POST, getBaseUrl("auth", "login"))
                              .body(user)
                              .exchange()
                              .getResponseBody();

        assertNotNull(response);
        assertTrue(!response.getJwt().isEmpty());
    }

    // @Test
    public void testFailLogin() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("what");

        var response = WebCall.of(restTemplate, Jwt.class)
                              .request(HttpMethod.POST, getBaseUrl("auth", "login"))
                              .body(user)
                              .skipRestClientError(true)
                              .exchange()
                              .getOptionalResponseBody();

        assertTrue(response.isEmpty());
    }

    // @Test
    public void testPatchUser() {
        User user = this.testGetUser(1L);
        user.setUsername("something");
        user.setPassword("something");
        user.setEmail("something@gmail.com");

        var response = WebCall.of(restTemplate, User.class)
                              .request(HttpMethod.POST, getBaseUrl("users"))
                              .header("X-HTTP-Method-Override", "PATCH")
                              .body(user)
                              .exchange()
                              .getResponseBody();

        testLogin("something", "something");

        user = this.testGetUser(1L);
        user.setUsername("admin");
        user.setPassword("admin");
        user.setEmail("admin@gmail.com");

        response = WebCall.of(restTemplate, User.class)
                              .request(HttpMethod.POST, getBaseUrl("users"))
                              .header("X-HTTP-Method-Override", "PATCH")
                              .body(user)
                              .exchange()
                              .getResponseBody();

        testLogin("admin", "admin");
    }

    private String getBaseUrl(String... path) {
        return String.format("%s/%s", baseUrl, String.join("/", path));
    }
}
