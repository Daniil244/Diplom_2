package order;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserSpec;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class OrderListTest {
    private String userAccessToken;
    private int numberOfOrders;

    User user = new User();

    @Before
    public void tearUp() throws Exception {

        user = User.getRandomUser(); // Создание пользователя
        userAccessToken = new UserSpec().getResponseCreateUser(user,200).accessToken;
        numberOfOrders = 4; // Количество заказов пользователя
        new OrderSpec().createListOfOrders(user, numberOfOrders); // Создание списка заказов пользователя
    }

    // Удаление учетной записи пользователя
    @After
    public void tearDown() throws Exception {
        new UserSpec().getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Тест успешного получения списка заказов авторизованного пользователя")
    public void successfulGetOfOrdersListFromAuthorizedUserTestOk() throws JsonProcessingException {
        // Авторизацию пользователя
        userAccessToken = new UserSpec().getResponseUserAuthorization(user, 200).accessToken;
        // Получения списка заказов пользователя
        ArrayList<Integer> orderNumber =
                new ArrayList<>(new OrderSpec().getAnOrderListRequestResponse(userAccessToken, 200)
                        .extract()
                        .path("orders.number"));
        assertEquals(numberOfOrders, orderNumber.size());
    }

    @Test
    @DisplayName("Тест неуспешного получения списка заказов неавторизованного пользователя")
    public void failGetOfOrdersListFromUnauthorizedUserTestOk() throws JsonProcessingException {
        // Получения списка заказов пользователя
        new OrderSpec().getAnOrderListRequestResponse("", 401)
                .body("message",equalTo("You should be authorised"));
    }
}