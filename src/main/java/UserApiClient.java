import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserApiClient extends RestClient {
    private final String userBaseUri = "/api/auth";
    @Step("Создать пользователя")
    public ValidatableResponse create(UserRegistrationRequest userRegistrationRequest) {
        return given()
                .spec(configureSpecification())
                .body(userRegistrationRequest)
                .when()
                .post(userBaseUri + "/register")
                .then();
    }
    @Step("Авторизоваться под пользователем")
    public ValidatableResponse login(UserLoginRequest userLoginRequest) {
        return given()
                .spec(configureSpecification())
                .body(userLoginRequest)
                .when()
                .post(userBaseUri + "/login")
                .then();
    }
    @Step("Удалить пользователя")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .spec(configureSpecification())
                .header("Authorization", accessToken)
                .when()
                .delete(userBaseUri + "/user")
                .then();
    }
    @Step("Получить информацию о пользователе")
    public ValidatableResponse getData(String accessToken) {
        return given()
                .spec(configureSpecification())
                .header("Authorization", accessToken)
                .when()
                .get(userBaseUri + "/user")
                .then();
    }
    @Step("Обновить информацию о пользователе")
    public ValidatableResponse edit(String accessToken, UserRegistrationRequest userRegistrationRequest) {
        return given()
                .spec(configureSpecification())
                .header("Authorization", accessToken)
                .body(userRegistrationRequest)
                .when()
                .patch(userBaseUri + "/user")
                .then();
    }
}
