import org.apache.commons.lang3.RandomStringUtils;

public class OrderDataProvider {

    // ID ингредиентов
    private static final String[] ingredients = new String[]{
            "61c0c5a71d1f82001bdaaa6d",
            "61c0c5a71d1f82001bdaaa6f",
            "61c0c5a71d1f82001bdaaa70",
            "61c0c5a71d1f82001bdaaa71",
            "61c0c5a71d1f82001bdaaa72",
            "61c0c5a71d1f82001bdaaa6e",
            "61c0c5a71d1f82001bdaaa73",
            "61c0c5a71d1f82001bdaaa74",
            "61c0c5a71d1f82001bdaaa6c",
            "61c0c5a71d1f82001bdaaa75",
            "61c0c5a71d1f82001bdaaa76",
            "61c0c5a71d1f82001bdaaa77",
            "61c0c5a71d1f82001bdaaa78",
            "61c0c5a71d1f82001bdaaa79",
            "61c0c5a71d1f82001bdaaa7a"};


    public static OrderCreationRequest getRandomCreateOrderRequest() {
        OrderCreationRequest orderCreationRequest = new OrderCreationRequest();
        orderCreationRequest.setAssignIngredients(ingredients);
        return orderCreationRequest;
    }

    public static OrderCreationRequest getNullIngredients() {
        OrderCreationRequest orderCreationRequest = new OrderCreationRequest();
        orderCreationRequest.setAssignIngredients(new String[]{});
        return orderCreationRequest;
    }

    public static OrderCreationRequest getInvalidIngredients() {
        OrderCreationRequest orderCreationRequest = new OrderCreationRequest();
        orderCreationRequest.setAssignIngredients(new String[] {RandomStringUtils.randomAlphabetic(24)});

        return orderCreationRequest;
    }
}