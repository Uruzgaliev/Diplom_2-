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

public class UserDataUpdateTest {
    private final UserApiClient apiClient = new UserApiClient();
    private String userToken;

    @Before
    public void setup() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @Step("Обновить данные пользователя (с авторизацией)")
    @DisplayName("Успешное обновление данных пользователя")
    @Description("Ожидается статус 200, содержание поля success (true), обновленные email и name")
    public void shouldSuccessfullyUpdateUserData() {
        UserRegistrationRequest newUser = UserDataGenerator.generateRandomUserRequest(); // Создание нового пользователя
        userToken = apiClient.create(newUser)
                .statusCode(200)
                .extract().jsonPath().get("accessToken");

        // Генерация нового запроса для обновления данных
        UserRegistrationRequest updatedUser = UserDataGenerator.generateRandomUserRequest();
        apiClient.edit(userToken, updatedUser)
                .statusCode(200)
                .body("success", Matchers.is(true))
                .and()
                .body("user.email", Matchers.is(updatedUser.getEmail().toLowerCase()))
                .and()
                .body("user.name", Matchers.is(updatedUser.getName()));
    }

    @Test
    @Step("Попытка обновления данных пользователя без авторизации")
    @DisplayName("Ошибка обновления пользователя без авторизации")
    @Description("Ожидается статус 401, тело ответа содержит success (false) и сообщение о необходимости авторизации")
    public void shouldNotUpdateUserDataWithoutAuth() {
        UserRegistrationRequest userRequest = UserDataGenerator.generateRandomUserRequest();

        // Попытка обновления без токена
        userToken = "";
        apiClient.edit(userToken, userRequest)
                .statusCode(401)
                .body("success", Matchers.is(false))
                .and()
                .body("message", Matchers.is("You should be authorised"));
    }

    @Test
    @Step("Попытка обновления email на существующий")
    @DisplayName("Неудача при обновлении, если email уже занят")
    @Description("Ожидается статус 403, тело ответа содержит success (false) и сообщение о существующем пользователе с таким email")
    public void shouldNotUpdateUserWithExistingEmail() {
        // Регистрация первого пользователя
        UserRegistrationRequest user1 = UserDataGenerator.generateRandomUserRequest();
        userToken = apiClient.create(user1)
                .statusCode(200)
                .extract().jsonPath().get("accessToken");

        // Регистрация второго пользователя
        UserRegistrationRequest user2 = UserDataGenerator.generateRandomUserRequest();
        apiClient.create(user2);

        // Попытка обновления email на уже существующий
        apiClient.edit(userToken, user2)
                .statusCode(403)
                .body("success", Matchers.is(false))
                .and()
                .body("message", Matchers.is("User with such email already exists"));
    }

    @After
    public void cleanup() {
        // Удаление пользователя, если токен действителен
        if (userToken != null && !userToken.isEmpty()) {
            apiClient.delete(userToken)
                    .statusCode(202);
        }
    }
}