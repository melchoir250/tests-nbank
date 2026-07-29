package api;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("POST /api/v1/admin/users")
class CreateUserApiTest {

  @Test
  @DisplayName("Возвращает все ошибки имени пользователя независимо от порядка")
  void shouldReturnAllUsernameValidationErrorsInAnyOrder() {
    CreateUserRequest invalidUser = RandomModelGenerator.generate(CreateUserRequest.class);
    invalidUser.setUsername("");

    AdminSteps.createUserExpectingInvalidUsername(invalidUser);
  }
}
