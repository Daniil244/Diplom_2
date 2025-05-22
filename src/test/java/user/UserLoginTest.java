package user;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserLoginTest {
    private boolean userAuthorisationSuccess;
    private String userAccessToken;

    User user = new User();

    @Before
    public void tearUp() throws Exception {
        user = User.getRandomUser();
        userAccessToken = new UserSpec().getResponseCreateUser(user,200).accessToken;
    }

    @After
    public void tearDown() throws Exception {
        new UserSpec().getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Тест успешной авторизации под существующим пользователем")
    public void successfulAuthorizationUserTestOk() throws JsonProcessingException {
        User createdUser = new User(user.getEmail(), user.getPassword());
        UserSpec response = new UserSpec().getResponseUserAuthorization(createdUser, 200);
        userAccessToken = response.accessToken;
        userAuthorisationSuccess = response.success;
        assertTrue(userAuthorisationSuccess);
    }

    @Test
    @DisplayName("Тест неуспешной авторизации под существующим пользователем с неверным логином (email)")
    public void failAuthorizationUserWithInvalidEmailTestOk() throws JsonProcessingException {
        String invalidEmail = "Invalid" + user.getEmail();
        User createdUser = new User(invalidEmail, user.getPassword());
        userAuthorisationSuccess = new UserSpec().getResponseUserAuthorization(createdUser, 401).success;
        assertFalse(userAuthorisationSuccess);
    }

    @Test
    @DisplayName("Тест неуспешной авторизации под существующим пользователем с неверным паролем")
    public void failAuthorizationUserWithInvalidPasswordTestOk() throws JsonProcessingException {
        String invalidPassword = "Invalid" + user.getPassword();
        User createdUser = new User(user.getEmail(), invalidPassword);
        userAuthorisationSuccess =  new UserSpec().getResponseUserAuthorization(createdUser, 401).success;
        assertFalse(userAuthorisationSuccess);
    }
}