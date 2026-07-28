package common.storage;

import api.models.CreateUserRequest;
import api.requests.steps.UserSteps;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class SessionStorage {
  private static final ThreadLocal<LinkedHashMap<CreateUserRequest, UserSteps>> USER_STEPS = ThreadLocal
      .withInitial(LinkedHashMap::new);

  private SessionStorage() {
  }

  public static void addUsers(List<CreateUserRequest> users) {
    for (CreateUserRequest user : users) {
      USER_STEPS.get().put(user, new UserSteps(user.getUsername(), user.getPassword()));
    }
  }

  public static CreateUserRequest getUser(int number) {
    return new ArrayList<>(USER_STEPS.get().keySet()).get(number - 1);
  }

  public static CreateUserRequest getUser() {
    return getUser(1);
  }

  public static UserSteps getSteps(int number) {
    return new ArrayList<>(USER_STEPS.get().values()).get(number - 1);
  }

  public static UserSteps getSteps() {
    return getSteps(1);
  }

  public static void clear() {
    USER_STEPS.remove();
  }
}
