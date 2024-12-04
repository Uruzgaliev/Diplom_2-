import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;


import static io.restassured.RestAssured.given;

public class OrderApiClient extends RestClient {
    private final String orderBaseUri = "/api";

    @Step("Создать заказ с авторизацией")
    public ValidatableResponse createWithAuth(OrderCreationRequest orderCreationRequest, String accessToken) {
        return given()
                .spec(configureSpecification())
                .header("Authorization", accessToken)
                .body(orderCreationRequest)
                .when()
                .post(orderBaseUri + "/orders")
                .then();
    }
    @Step("Создать заказ без авторизации")
    public ValidatableResponse createWithoutAuth(OrderCreationRequest orderCreationRequest) {
        return given()
                .spec(configureSpecification())
                .body(orderCreationRequest)
                .when()
                .post(orderBaseUri + "/orders")
                .then();
    }

    @Step("Получить данные об ингредиентах")
    public ValidatableResponse getDataIngredients() {
        return given()
                .spec(configureSpecification())
                .when()
                .get(orderBaseUri + "/ingredients")
                .then();
    }

    @Step("Получить заказы конкретного пользователя (с авторизацией)")
    public ValidatableResponse getOrdersUserWithAuth(String accessToken) {
        return given()
                .spec(configureSpecification())
                .header("Authorization", accessToken)
                .when()
                .get(orderBaseUri + "/orders")
                .then();

    }
    @Step("Получить заказы конкретного пользователя (без авторизации)")
    public ValidatableResponse getOrdersUserWithoutAuth() {
        return given()
                .spec(configureSpecification())
                .when()
                .get(orderBaseUri + "/orders")
                .then();
    }

}
