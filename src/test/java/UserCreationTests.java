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

import java.util.Objects;

public class UserCreationTests {
    private final UserApiClient userApi = new UserApiClient();
    private String token;

    @Before
    public void setup() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @Step("Создание нового уникального пользователя")
    @DisplayName("Успешная регистрация уникального пользователя")
    @Description("Ожидаемый статус ответа: 200, необходимо наличие полей success (true), email, name, accessToken, refreshToken")
    public void shouldCreateUniqueUser() {
        UserRegistrationRequest newUser = UserDataGenerator.generateRandomUserRequest();
        // Регистрация нового пользователя и проверка ответа
        token = userApi.create(newUser)
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
    @Step("Попытка зарегистрировать уже существующего пользователя")
    @DisplayName("Пользователь с существующим email не будет создан")
    @Description("Ожидается статус 403, тело ответа содержит success false и сообщение 'User already exists'")
    public void shouldNotCreateExistingUser() {
        UserRegistrationRequest existingUser = UserDataGenerator.generateRandomUserRequest();
        // Создание пользователя
        token = userApi.create(existingUser)
                .statusCode(200)
                .extract().jsonPath().get("accessToken");
        // Повторная попытка создания
        userApi.create(existingUser)
                .statusCode(403)
                .body("success", Matchers.is(false))
                .and()
                .body("message", Matchers.is("User already exists"));
    }

    @Test
    @Step("Попытка создания пользователя без указания email")
    @DisplayName("Ошибка при создании пользователя без email")
    @Description("Ожидается статус 403, тело ответа содержит success false и сообщение о требуемых полях")
    public void shouldNotCreateUserWithoutEmail() {
        UserRegistrationRequest userWithoutEmail = UserDataGenerator.generateUserWithoutEmailRequest();
        userApi.create(userWithoutEmail)
                .statusCode(403)
                .body("success", Matchers.is(false))
                .and()
                .body("message", Matchers.is("Email, password and name are required fields"));
    }

    @Test
    @Step("Попытка создания пользователя без указания пароля")
    @DisplayName("Ошибка при создании пользователя без password")
    @Description("Ожидается статус 403, тело ответа содержит success false и сообщение о требуемых полях")
    public void shouldNotCreateUserWithoutPassword() {
        UserRegistrationRequest userWithoutPassword = UserDataGenerator.generateUserWithoutPasswordRequest(); // Используется новое название метода
        userApi.create(userWithoutPassword)
                .statusCode(403)
                .body("success", Matchers.is(false))
                .and()
                .body("message", Matchers.is("Email, password and name are required fields"));
    }

    @Test
    @Step("Попытка создания пользователя без указания имени")
    @DisplayName("Ошибка при создании пользователя без name")
    @Description("Ожидается статус 403, тело ответа содержит success false и сообщение о требуемых полях")
    public void shouldNotCreateUserWithoutName() {
        UserRegistrationRequest userWithoutName = UserDataGenerator.generateUserWithoutNameRequest(); // Используется новое название метода
        userApi.create(userWithoutName)
                .statusCode(403)
                .body("success", Matchers.is(false))
                .and()
                .body("message", Matchers.is("Email, password and name are required fields"));
    }

    @After
    public void cleanUp() {
        if (!(Objects.isNull(token))) {
            userApi.delete(token)
                    .statusCode(202);
        }
    }
}
