package common.extensions;

import api.models.CreateUserRequest;
import api.requests.steps.AdminSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

public class UserSessionExtension implements BeforeEachCallback {
  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
    if (annotation == null) {
      return;
    }

    int userCount = annotation.value();
    SessionStorage.clear();

    List<CreateUserRequest> users = new LinkedList<>();
    for (int i = 0; i < userCount; i++) {
      users.add(AdminSteps.createUser());
    }
    SessionStorage.addUsers(users);

    BasePage.authAsUser(SessionStorage.getUser(annotation.auth()));
  }
}
