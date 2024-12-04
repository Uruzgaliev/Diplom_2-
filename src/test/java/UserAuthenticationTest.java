import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserAuthenticationTest {
    private final UserApiClient apiUserClient = new UserApiClient(); // переименовано
    private String userToken;

    @Before
    public void initialize() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @Step("Авторизация существующего пользователя")
    @DisplayName("Успешная авторизация с корректными данными")
    @Description("Ожидаем статус 200 с успешным ответом, содержащим токены и информацию о пользователе")
    public void shouldAuthenticateExistingUser() {
        UserRegistrationRequest newUser = UserDataGenerator.generateRandomUserRequest();
        // Регистрация пользователя
        apiUserClient.create(newUser);

        // Авторизация
        UserLoginRequest loginRequest = UserLoginRequest.from(newUser);
        userToken = apiUserClient.login(loginRequest)
                .statusCode(200)
                .body("success", Matchers.is(true))
                .and()
                .body("user.email", Matchers.is(newUser.getEmail().toLowerCase()))
                .and()
                .body("user.name", Matchers.is(newUser.getName()))
                .and()
                .body("accessToken", Matchers.notNullValue())
                .and()
                .body("refreshToken", Matchers.notNullValue())
                .extract().jsonPath().get("accessToken");
    }

    @Test
    @Step("Попытка авторизации с неправильным логином")
    @DisplayName("Не удаётся авторизоваться с неверным email")
    @Description("Ожидаем статус 401, тело ответа содержит сообщение об ошибке")
    public void shouldFailAuthWithInvalidEmail() {
        UserRegistrationRequest newUser = UserDataGenerator.generateRandomUserRequest();
        // Регистрация пользователя
        apiUserClient.create(newUser);

        // Попытка авторизации с некорректным email
        UserLoginRequest loginRequest = UserLoginRequest.from(newUser);
        loginRequest.setEmail(RandomStringUtils.randomAlphabetic(8));

        apiUserClient.login(loginRequest)
                .statusCode(401)
                .body("success", Matchers.is(false))
                .and()
                .body("message", Matchers.is("email or password are incorrect"));
    }

    @Test
    @Step("Попытка авторизации с неверным паролем")
    @DisplayName("Не удаётся авторизоваться с некорректным паролем")
    @Description("Ожидаем статус 401, тело ответа содержит сообщение об ошибке")
    public void shouldFailAuthWithInvalidPassword() {
        UserRegistrationRequest newUser = UserDataGenerator.generateRandomUserRequest();
        // Регистрация пользователя
        apiUserClient.create(newUser);

        // Попытка авторизации с некорректным паролем
        UserLoginRequest loginRequest = UserLoginRequest.from(newUser);
        loginRequest.setPassword(RandomStringUtils.randomAlphabetic(8));

        apiUserClient.login(loginRequest)
                .statusCode(401)
                .body("success", Matchers.is(false))
                .and()
                .body("message", Matchers.is("email or password are incorrect"));
    }

    @After
    public void cleanUp() {
        if (userToken != null) {
            apiUserClient.delete(userToken)
                    .statusCode(202);
        }
    }
}
