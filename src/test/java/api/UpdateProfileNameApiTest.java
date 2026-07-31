package api;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import constants.ProfileLimits;
import api.dao.UserDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomData;
import api.models.CustomerProfile;
import api.requests.steps.CustomerContext;
import api.requests.steps.DataBaseSteps;
import api.requests.steps.ProfileSteps;
import common.annotations.APIVersion;

@APIVersion("with_database")
@DisplayName("PUT /api/v1/customer/profile")
class UpdateProfileNameApiTest extends BaseApiTest {

  @ParameterizedTest
  @MethodSource("positiveNames")
  @DisplayName("Принимает допустимое имя профиля")
  void shouldAcceptValidProfileName(String newName) {
    CustomerContext customer = CustomerContext.create();
    customer.assertProfileName(null);

    ProfileSteps.updateProfileName(customer.spec(), customer.profileNameRequest(newName));

    CustomerProfile profile = ProfileSteps.getProfile(customer.spec());
    UserDao userDao = DataBaseSteps.getUserByUsername(customer.username());
    DaoAndModelAssertions.assertThat(profile, userDao).match();
  }

  static Stream<Arguments> positiveNames() {
    return Stream.of(
      Arguments.of(RandomData.validProfileName()),
      Arguments.of(ProfileLimits.VALID_ONE_CHAR),
      Arguments.of(ProfileLimits.VALID_MAX_LENGTH));
  }

  @ParameterizedTest
  @MethodSource("negativeNames")
  @DisplayName("Отклоняет недопустимое имя профиля")
  void shouldRejectInvalidProfileName(String newName) {
    CustomerContext customer = CustomerContext.create();
    customer.assertProfileName(null);

    ProfileSteps.updateProfileNameExpectingBadRequest(
      customer.spec(),
      customer.profileNameRequest(newName));

    customer.assertProfileName(null);
    UserDao userDao = DataBaseSteps.getUserByUsername(customer.username());
    softly.assertThat(userDao.getName()).isNull();
  }

  static Stream<Arguments> negativeNames() {
    return Stream.of(
      Arguments.of(ProfileLimits.EMPTY),
      Arguments.of(ProfileLimits.WITH_DIGIT),
      Arguments.of(ProfileLimits.WITH_SPECIAL),
      Arguments.of(ProfileLimits.THREE_WORDS));
  }
}
