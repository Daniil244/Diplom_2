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
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderTest {
    private String userAccessToken;
    private ArrayList<String> ingredientsHash;
    private String[] ingredients;

    User user = new User();

    @Before
    public void tearUp() throws Exception {
        user = User.getRandomUser();
        userAccessToken = new UserSpec().getResponseCreateUser(user,200).accessToken;
        ingredientsHash = new OrderSpec().getCreatedListOfValidHashesOfIngredients();
    }

    @After
    public void tearDown() throws Exception {
        new UserSpec().getResponseUserDeleted(userAccessToken, 202);
    }

    @Test
    @DisplayName("Тест успешного создания заказа с авторизацией с двумя ингредиентами")
    public void successfulCreateOrderWithAuthorizationAndTwoIngredientsTestOk() throws JsonProcessingException {
        userAccessToken = new UserSpec().getResponseUserAuthorization(user, 200).accessToken;
        ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};
        Order order = new Order(ingredients);
        new OrderSpec().getResponseCreateOrder(order, userAccessToken, 200)
                .assertThat()
                .body("order.number",notNullValue());
    }

    @Test
    @DisplayName("Тест неуспешного создания заказа с авторизацией без ингредиентов")
    public void failCreateOrderWithAuthorizationAndZeroIngredientTestOk() throws JsonProcessingException {
        userAccessToken = new UserSpec().getResponseUserAuthorization(user, 200).accessToken;
        Order order = new Order(ingredients);
        new OrderSpec().getResponseCreateOrder(order, userAccessToken, 400)
                .body("message",equalTo("Ingredient ids must be provided"));
    }


    @Test
    @DisplayName("Тест неуспешного создания заказа без авторизации с двумя ингредиентами") // Тест падает
    public void failCreateOrderWithoutAuthorizationAndTwoIngredientTestOk() throws JsonProcessingException {
        ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};
        Order order = new Order(ingredients);
        new OrderSpec().getResponseCreateOrder(order, "", 200)
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Тест неуспешного создания заказа без авторизации без ингредиентов")
    public void failCreateOrderWithoutAuthorizationAndZeroIngredientTestOk() throws JsonProcessingException {
        Order order = new Order(ingredients);
        new OrderSpec().getResponseCreateOrder(order, "", 400)
                .body("message",equalTo("Ingredient ids must be provided"));
    }


    @Test
    @DisplayName("Тест неуспешного создания заказа с авторизацией и неверным хешем ингредиента") // Тест падает
    public void failCreateOrderWithAuthorizationAndIncorrectHashIngredientTestOk() throws JsonProcessingException {
        userAccessToken = new UserSpec().getResponseUserAuthorization(user, 200).accessToken;
        ingredients = new String[]{"123456789012345678901234"};
        Order order = new Order(ingredients);
        new OrderSpec().getResponseCreateOrder(order, userAccessToken, 500)
                .body("message",equalTo("Internal Server Error"));
    }
}
