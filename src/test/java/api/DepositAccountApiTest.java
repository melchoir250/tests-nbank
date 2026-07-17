package api;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import constants.DepositLimits;
import generators.RandomData;
import models.DepositRequest;
import models.DepositResponse;
import models.comparison.ModelAssertions;
import requests.steps.CustomerContext;
import requests.steps.UserSteps;

@DisplayName("POST /api/v1/accounts/deposit")
class DepositAccountApiTest extends BaseApiTest {

  @ParameterizedTest
  @MethodSource("positiveDepositAmounts")
  void shouldAcceptValidDepositAmount(double depositAmount) {
    CustomerContext customer = CustomerContext.create()
      .withAccount();

    DepositRequest depositRequest = customer.depositRequest(depositAmount);
    DepositResponse deposit = UserSteps.deposit(customer.spec(), depositRequest);

    ModelAssertions.assertThatModels(depositRequest, deposit)
      .match();
    customer.assertBalance(depositAmount);
  }

  static Stream<Arguments> positiveDepositAmounts() {
    return Stream.of(
      Arguments.of(RandomData.depositAmount()),
      Arguments.of(DepositLimits.MIN),
      Arguments.of(DepositLimits.MAX),
      Arguments.of(DepositLimits.JUST_BELOW_MAX));
  }

  @ParameterizedTest
  @MethodSource("belowMinDepositAmounts")
  void shouldRejectDepositBelowMinimum(double depositAmount) {
    CustomerContext customer = CustomerContext.create()
      .withAccount();

    UserSteps.depositExpectingMinAmountError(customer.spec(),
      customer.depositRequest(depositAmount));
    customer.assertBalance(0);
  }

  static Stream<Arguments> belowMinDepositAmounts() {
    return Stream.of(
      Arguments.of(DepositLimits.NEGATIVE),
      Arguments.of(DepositLimits.ZERO));
  }

  @Test
  void shouldRejectDepositAboveMaximum() {
    CustomerContext customer = CustomerContext.create()
      .withAccount();

    UserSteps.depositExpectingMaxAmountError(
      customer.spec(),
      customer.depositRequest(DepositLimits.ABOVE_MAX));
    customer.assertBalance(0);
  }

  @Test
  void shouldRejectDepositToNonExistingAccount() {
    CustomerContext customer = CustomerContext.create()
      .withAccount();

    UserSteps.depositExpectingForbidden(
      customer.spec(),
      UserSteps.depositRequest(DepositLimits.NON_EXISTING_ACCOUNT_ID, RandomData.depositAmount()));
    customer.assertBalance(0);
  }

  @Test
  void shouldRejectDepositWithoutAuthorization() {
    UserSteps.depositExpectingUnauthorized(
      UserSteps.depositRequest(
        DepositLimits.UNAUTHORIZED_TEST_ACCOUNT_ID,
        RandomData.depositAmount()));
  }
}
