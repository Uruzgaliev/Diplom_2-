import org.apache.commons.lang3.RandomStringUtils;

public class UserDataGenerator {
    // Генератор Email
    private static final String EMAIL_DOMAIN = "@yandex.ru";

    public static String createRandomEmail() {
        String emailPrefix = RandomStringUtils.randomAlphabetic(7);
        return emailPrefix + EMAIL_DOMAIN;
    }

    // Получение данных
    public static UserRegistrationRequest generateRandomUserRequest() {
        UserRegistrationRequest userRequest = new UserRegistrationRequest();
        userRequest.setEmail(createRandomEmail());
        userRequest.setPassword(RandomStringUtils.randomAlphanumeric(8));
        userRequest.setName(RandomStringUtils.randomAlphabetic(7));

        return userRequest;
    }

    public static UserRegistrationRequest generateUserWithoutEmailRequest() {
        UserRegistrationRequest userRequest = new UserRegistrationRequest();
        userRequest.setEmail(null);
        userRequest.setPassword(RandomStringUtils.randomAlphanumeric(8));
        userRequest.setName(RandomStringUtils.randomAlphabetic(7));

        return userRequest;
    }

    public static UserRegistrationRequest generateUserWithoutPasswordRequest() {
        UserRegistrationRequest userRequest = new UserRegistrationRequest();
        userRequest.setEmail(createRandomEmail());
        userRequest.setPassword(""); // Установка пустого значения
        userRequest.setName(RandomStringUtils.randomAlphabetic(7));

        return userRequest;
    }

    public static UserRegistrationRequest generateUserWithoutNameRequest() {
        UserRegistrationRequest userRequest = new UserRegistrationRequest();
        userRequest.setEmail(createRandomEmail());
        userRequest.setPassword(RandomStringUtils.randomAlphanumeric(8));
        userRequest.setName(""); // Установка пустого значения

        return userRequest;
    }
}