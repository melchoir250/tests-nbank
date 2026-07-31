package api;

import static org.junit.jupiter.api.Assertions.assertNull;
import api.dao.UserDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.comparison.ModelAssertions;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ApiRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.DataBaseSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.APIVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("POST /api/v1/admin/users")
class CreateUserApiTest {

  @Test
  @APIVersion("with_validation_fix")
  @DisplayName("Возвращает все ошибки имени пользователя независимо от порядка")
  void shouldReturnAllUsernameValidationErrorsInAnyOrder() {
    CreateUserRequest invalidUser = RandomModelGenerator.generate(CreateUserRequest.class);
    invalidUser.setUsername("");

    AdminSteps.createUserExpectingInvalidUsername(invalidUser);
  }

  @Test
  @APIVersion("with_database")
  @DisplayName("Сохраняет созданного пользователя в базе данных")
  void shouldPersistCreatedUserInDatabase() {
    CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

    CreateUserResponse createUserResponse = new ApiRequester(
        RequestSpecs.adminSpec(),
        Endpoint.ADMIN_CREATE_USER,
        ResponseSpecs.entityWasCreated())
        .postAndExtract(createUserRequest);

    ModelAssertions.assertThatModels(createUserRequest, createUserResponse).match();

    UserDao userDao = DataBaseSteps.getUserByUsername(createUserRequest.getUsername());
    DaoAndModelAssertions.assertThat(createUserResponse, userDao).match();
  }

  @Test
  @APIVersion("with_database")
  @DisplayName("Не создаёт пользователя с невалидным username в базе данных")
  void shouldNotPersistInvalidUserInDatabase() {
    CreateUserRequest invalidUser = RandomModelGenerator.generate(CreateUserRequest.class);
    invalidUser.setUsername("");

    new ApiRequester(
        RequestSpecs.adminSpec(),
        Endpoint.ADMIN_CREATE_USER,
        ResponseSpecs.invalidUsernameErrors())
        .post(invalidUser);

    assertNull(DataBaseSteps.getUserByUsername(invalidUser.getUsername()));
  }
}
