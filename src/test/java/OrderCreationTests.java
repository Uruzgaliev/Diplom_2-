import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class OrderCreationTests {
    private final UserApiClient userApiClient = new UserApiClient();
    private final OrderApiClient orderApiClient = new OrderApiClient();
    private String authToken;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @Step("Создание заказа для авторизованного пользователя")
    @DisplayName("Успешное создание заказа с авторизацией и корректными ингредиентами")
    @Description("Ожидаемый статус кода: 200, ответ содержит success (true), номер заказа и имя")
    public void createOrderWithAuth() {
        UserRegistrationRequest registrationRequest = UserDataGenerator.generateRandomUserRequest();
        // Регистрация пользователя и получение токена
        authToken = userApiClient.create(registrationRequest)
                .extract().jsonPath().getString("accessToken");

        // Создание заказа с использованием корректных ингредиентов
        OrderCreationRequest orderRequest = OrderDataProvider.getRandomCreateOrderRequest();

        // Отладочный вывод для проверки, что мы передаем правильные ингредиенты
        System.out.println("Ingredient list: " + Arrays.toString(orderRequest.getIngredients()));

        // Выполнение запроса на создание заказа
        ValidatableResponse response = orderApiClient.createWithAuth(orderRequest, authToken);

        // Проверка статуса ответа
        response
                .statusCode(200) // Ожидается статус 200
                .body("name", Matchers.notNullValue())
                .body("order.number", Matchers.notNullValue())
                .body("success", Matchers.equalTo(true));
    }

    @Test
    @Step("Создание заказа для неавторизованного пользователя")
    @DisplayName("Успешное создание заказа без авторизации и с корректными ингредиентами")
    @Description("Ожидаемый статус кода: 200, ответ содержит success (true), номер заказа и имя")
    public void createOrderWithoutAuth() {
        UserRegistrationRequest registrationRequest = UserDataGenerator.generateRandomUserRequest();
        // Регистрация пользователя и получение токена
        authToken = userApiClient.create(registrationRequest)
                .extract().jsonPath().getString("accessToken");

        // Создание заказа
        OrderCreationRequest orderRequest = OrderDataProvider.getRandomCreateOrderRequest();
        orderApiClient.createWithoutAuth(orderRequest)
                .statusCode(200)
                .body("name", Matchers.notNullValue())
                .and()
                .body("order.number", Matchers.notNullValue())
                .and()
                .body("success", Matchers.equalTo(true));
    }

    @Test
    @Step("Создание заказа без ингредиентов")
    @DisplayName("Заказ не должен создаваться без ингредиентов")
    @Description("Ожидаемый статус кода: 400, ответ содержит success (false) и сообщение об ошибке")
    public void notCreateOrderWithoutIngredients() {
        UserRegistrationRequest registrationRequest = UserDataGenerator.generateRandomUserRequest();
        authToken = userApiClient.create(registrationRequest)
                .extract().jsonPath().getString("accessToken");

        // Попытка создания заказа без ингредиентов
        OrderCreationRequest orderRequest = OrderDataProvider.getNullIngredients(); // Используем метод для создания заказа без ингредиентов
        orderApiClient.createWithAuth(orderRequest, authToken)
                .statusCode(400)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Step("Создание заказа с неверным идентификатором ингредиента")
    @DisplayName("Заказ не должен создаваться при передаче некорректного ингредиента")
    @Description("Ожидаемый статус кода: 500 при наличии неверного хеша ингредиента")
    public void notCreateOrderWithInvalidIngredients() {
        UserRegistrationRequest registrationRequest = UserDataGenerator.generateRandomUserRequest();
        authToken = userApiClient.create(registrationRequest)
                .extract().jsonPath().getString("accessToken");

        // Попытка создания заказа с неверным идентификатором
        OrderCreationRequest orderRequest = OrderDataProvider.getInvalidIngredients();
        orderApiClient.createWithAuth(orderRequest, authToken)
                .statusCode(500); // Ожидаем 500
    }

    @After
    public void tearDown() {
        if (authToken != null && !authToken.isEmpty()) {
            userApiClient.delete(authToken)
                    .statusCode(202);
        }
    }
}