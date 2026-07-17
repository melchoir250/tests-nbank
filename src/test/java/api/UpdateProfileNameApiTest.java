package api;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import constants.ProfileLimits;
import api.generators.RandomData;
import api.requests.steps.CustomerContext;
import api.requests.steps.UserSteps;

@DisplayName("PUT /api/v1/customer/profile")
class UpdateProfileNameApiTest extends BaseApiTest {

  @ParameterizedTest
  @MethodSource("positiveNames")
  void shouldAcceptValidProfileName(String newName) {
    CustomerContext customer = CustomerContext.create();
    customer.assertProfileName(null);

    UserSteps.updateProfileName(customer.spec(), customer.profileNameRequest(newName));

    customer.assertProfileName(newName);
  }

  static Stream<Arguments> positiveNames() {
    return Stream.of(
      Arguments.of(RandomData.validProfileName()),
      Arguments.of(ProfileLimits.VALID_ONE_CHAR),
      Arguments.of(ProfileLimits.VALID_MAX_LENGTH));
  }

  @ParameterizedTest
  @MethodSource("negativeNames")
  void shouldRejectInvalidProfileName(String newName) {
    CustomerContext customer = CustomerContext.create();
    customer.assertProfileName(null);

    UserSteps.updateProfileNameExpectingBadRequest(
      customer.spec(),
      customer.profileNameRequest(newName));

    customer.assertProfileName(null);
  }

  static Stream<Arguments> negativeNames() {
    return Stream.of(
      Arguments.of(ProfileLimits.EMPTY),
      Arguments.of(ProfileLimits.WITH_DIGIT),
      Arguments.of(ProfileLimits.WITH_SPECIAL),
      Arguments.of(ProfileLimits.THREE_WORDS));
  }
}
