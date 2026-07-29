package api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import constants.DepositLimits;
import api.dao.AccountDao;
import api.dao.comparison.DaoAndModelAssertions;
import api.generators.RandomData;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.models.comparison.ModelAssertions;
import api.requests.steps.CustomerContext;
import api.requests.steps.DataBaseSteps;
import api.requests.steps.UserSteps;
import common.annotations.APIVersion;

@APIVersion("with_database")
@DisplayName("POST /api/v1/accounts/deposit")
class DepositAccountApiTest extends BaseApiTest {

  @ParameterizedTest
  @MethodSource("positiveDepositAmounts")
  @DisplayName("Принимает допустимую сумму пополнения")
  void shouldAcceptValidDepositAmount(double depositAmount) {
    CustomerContext customer = CustomerContext.create()
      .withAccount();

    DepositRequest depositRequest = customer.depositRequest(depositAmount);
    DepositResponse deposit = UserSteps.deposit(customer.spec(), depositRequest);

    ModelAssertions.assertThatModels(depositRequest, deposit)
      .match();
    DaoAndModelAssertions.assertThat(deposit, DataBaseSteps.getAccountById(customer.accountId()))
      .match();
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
  @DisplayName("Отклоняет пополнение ниже минимальной суммы")
  void shouldRejectDepositBelowMinimum(double depositAmount) {
    CustomerContext customer = CustomerContext.create()
      .withAccount();
    AccountDao accountBefore = DataBaseSteps.getAccountById(customer.accountId());

    UserSteps.depositExpectingMinAmountError(customer.spec(),
      customer.depositRequest(depositAmount));

    assertBalanceUnchanged(accountBefore);
  }

  static Stream<Arguments> belowMinDepositAmounts() {
    return Stream.of(
      Arguments.of(DepositLimits.NEGATIVE),
      Arguments.of(DepositLimits.ZERO));
  }

  @Test
  @DisplayName("Отклоняет пополнение выше максимальной суммы")
  void shouldRejectDepositAboveMaximum() {
    CustomerContext customer = CustomerContext.create()
      .withAccount();
    AccountDao accountBefore = DataBaseSteps.getAccountById(customer.accountId());

    UserSteps.depositExpectingMaxAmountError(
      customer.spec(),
      customer.depositRequest(DepositLimits.ABOVE_MAX));

    assertBalanceUnchanged(accountBefore);
  }

  @Test
  @DisplayName("Отклоняет пополнение несуществующего счёта")
  void shouldRejectDepositToNonExistingAccount() {
    CustomerContext customer = CustomerContext.create()
      .withAccount();
    AccountDao accountBefore = DataBaseSteps.getAccountById(customer.accountId());

    UserSteps.depositExpectingForbidden(
      customer.spec(),
      UserSteps.depositRequest(DepositLimits.NON_EXISTING_ACCOUNT_ID, RandomData.depositAmount()));

    assertBalanceUnchanged(accountBefore);
  }

  @Test
  @DisplayName("Отклоняет пополнение без авторизации")
  void shouldRejectDepositWithoutAuthorization() {
    CustomerContext customer = CustomerContext.create()
      .withAccount();
    AccountDao accountBefore = DataBaseSteps.getAccountById(customer.accountId());

    UserSteps.depositExpectingUnauthorized(customer.depositRequest(RandomData.depositAmount()));

    assertBalanceUnchanged(accountBefore);
  }

  private static void assertBalanceUnchanged(AccountDao accountBefore) {
    AccountDao accountAfter = DataBaseSteps.getAccountById(accountBefore.getId());
    assertThat(accountAfter.getBalance())
      .isCloseTo(accountBefore.getBalance(), offset(0.001));
  }
}
