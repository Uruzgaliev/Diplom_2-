import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserOrderRetrievalTest {
    private final UserApiClient apiUserClient = new UserApiClient();
    private final OrderApiClient apiOrderClient = new OrderApiClient();
    private String userToken;

    @Before
    public void init() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @Step("Получение списка заказов для авторизованного пользователя")
    @DisplayName("Успешное извлечение заказов для авторизованного пользователя")
    @Description("Ожидается статус-код: 200, тело ответа содержит ненулевые значения для ingredients, _id, status, number и временных меток")
    public void shouldRetrieveOrdersForAuthorizedUser() {
        UserRegistrationRequest newUser = UserDataGenerator.generateRandomUserRequest(); // Создаем нового пользователя
        userToken = apiUserClient.create(newUser)
                .extract().jsonPath().get("accessToken");

        // Создаем заказ
        OrderCreationRequest newOrderRequest = OrderDataProvider.getRandomCreateOrderRequest();
        Integer orderNumber = apiOrderClient.createWithAuth(newOrderRequest, userToken)
                .extract().jsonPath().get("order.number");

        // Извлечение заказов
        apiOrderClient.getOrdersUserWithAuth(userToken)
                .statusCode(200)
                .body("orders.ingredients", Matchers.notNullValue())
                .body("orders._id", Matchers.notNullValue())
                .body("orders.status", Matchers.notNullValue())
                .body("orders.number", Matchers.contains(orderNumber))
                .body("orders.createdAt", Matchers.notNullValue())
                .body("orders.updatedAt", Matchers.notNullValue())
                .body("total", Matchers.notNullValue())
                .body("totalToday", Matchers.notNullValue());
    }

    @Test
    @Step("Попытка получить список заказов для неавторизованного пользователя")
    @DisplayName("Запрос на получение заказов для неавторизованного пользователя не должен пройти")
    @Description("Ожидается статус-код: 401, тело ответа содержит сообщение об ошибке о необходимости авторизации")
    public void shouldNotRetrieveOrdersForUnauthorizedUser() {
        UserRegistrationRequest newUser = UserDataGenerator.generateRandomUserRequest();
        userToken = apiUserClient.create(newUser)
                .extract().jsonPath().get("accessToken");

        // Создание заказа
        OrderCreationRequest orderRequest = OrderDataProvider.getRandomCreateOrderRequest();
        apiOrderClient.createWithAuth(orderRequest, userToken);

        // Попытка получения списка заказов без токена
        apiOrderClient.getOrdersUserWithoutAuth()
                .statusCode(401)
                .body("message", Matchers.equalTo("You should be authorised"));
    }

    @After
    public void cleanup() {
        if (userToken != null && !userToken.isEmpty()) {
            apiUserClient.delete(userToken)
                    .statusCode(202);
        }
    }
}