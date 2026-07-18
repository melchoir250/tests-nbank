package ui;

import static com.codeborne.selenide.Selenide.executeJavaScript;

import api.BaseApiTest;
import api.models.CreateUserRequest;
import api.specs.RequestSpecs;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import ui.config.UiTestConfig;

public class BaseUiTest extends BaseApiTest {
    @BeforeAll
    public static void setupSelenoid() {
        UiTestConfig.apply();
    }

    public void authAsUser(String username, String password) {
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    }

    public void authAsUser(CreateUserRequest createUserRequest) {
        authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }
}
