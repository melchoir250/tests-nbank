package ui;

import api.BaseApiTest;
import api.models.CreateUserRequest;
import common.extensions.AdminSessionExtension;
import common.extensions.BrowserMatchExtension;
import common.extensions.UserSessionExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.config.UiTestConfig;
import ui.pages.BasePage;

@ExtendWith(AdminSessionExtension.class)
@ExtendWith(UserSessionExtension.class)
@ExtendWith(BrowserMatchExtension.class)
public class BaseUiTest extends BaseApiTest {
  @BeforeAll
  public static void setupSelenoid() {
    UiTestConfig.apply();
  }

  public void authAsUser(String username, String password) {
    BasePage.authAsUser(username, password);
  }

  public void authAsUser(CreateUserRequest createUserRequest) {
    BasePage.authAsUser(createUserRequest);
  }
}
