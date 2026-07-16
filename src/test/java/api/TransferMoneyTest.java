package api;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import constants.DepositLimits;
import generators.RandomData;
import models.DepositTransferRequest;
import models.DepositTransferResponse;
import models.comparison.ModelAssertions;
import requests.steps.CustomerContext;
import requests.steps.UserSteps;

@DisplayName("POST /api/v1/accounts/transfer")
class TransferMoneyTest extends BaseApiTest {

  @ParameterizedTest
  @MethodSource("positiveTransferAmounts")
  void shouldAcceptValidTransferAmount(double depositAmount, double transferAmount) {
    CustomerContext sender = CustomerContext.create()
      .withAccount()
      .withDeposit(depositAmount);
    CustomerContext receiver = CustomerContext.create()
      .withAccount();

    DepositTransferRequest transferRequest = sender.transferRequestTo(receiver, transferAmount);
    DepositTransferResponse transfer = UserSteps.transfer(sender.spec(), transferRequest);

    ModelAssertions.assertThatModels(transferRequest, transfer)
      .match();
    sender.assertBalance(depositAmount - transferAmount);
    receiver.assertBalance(transferAmount);
  }

  static Stream<Arguments> positiveTransferAmounts() {
    double randomDeposit = RandomData.depositAmount();
    return Stream.of(
      Arguments.of(randomDeposit, RandomData.transferAmount(randomDeposit)),
      Arguments.of(DepositLimits.MAX, DepositLimits.MIN),
      Arguments.of(DepositLimits.MAX, DepositLimits.JUST_BELOW_MAX));
  }

  @ParameterizedTest
  @MethodSource("transferNearMaxAmounts")
  void shouldAcceptTransferNearMaximumLimit(double transferAmount) {
    CustomerContext sender = CustomerContext.create()
      .withAccount()
      .withDeposits(DepositLimits.MAX, 2);
    double fundedBalance = sender.balance();
    CustomerContext receiver = CustomerContext.create()
      .withAccount();

    DepositTransferRequest transferRequest = sender.transferRequestTo(receiver, transferAmount);
    DepositTransferResponse transfer = UserSteps.transfer(sender.spec(), transferRequest);

    ModelAssertions.assertThatModels(transferRequest, transfer)
      .match();
    sender.assertBalance(fundedBalance - transferAmount);
    receiver.assertBalance(transferAmount);
  }

  static Stream<Arguments> transferNearMaxAmounts() {
    return Stream.of(
      Arguments.of(DepositLimits.TRANSFER_MAX),
      Arguments.of(DepositLimits.JUST_BELOW_TRANSFER_MAX));
  }

  @ParameterizedTest
  @MethodSource("invalidTransferAmounts")
  void shouldRejectInvalidTransferAmount(double transferAmount) {
    double depositAmount = RandomData.depositAmount();
    CustomerContext sender = CustomerContext.create()
      .withAccount()
      .withDeposit(depositAmount);
    CustomerContext receiver = CustomerContext.create()
      .withAccount();

    UserSteps.transferExpectingBadRequest(
      sender.spec(),
      sender.transferRequestTo(receiver, transferAmount));

    sender.assertBalance(depositAmount);
    receiver.assertBalance(0);
  }

  static Stream<Arguments> invalidTransferAmounts() {
    return Stream.of(
      Arguments.of(DepositLimits.ZERO),
      Arguments.of(DepositLimits.NEGATIVE));
  }

  @Test
  void shouldRejectTransferAboveMaximumLimit() {
    CustomerContext sender = CustomerContext.create()
      .withAccount()
      .withDeposits(DepositLimits.MAX, 3);
    double fundedBalance = sender.balance();
    CustomerContext receiver = CustomerContext.create()
      .withAccount();

    UserSteps.transferExpectingBadRequest(
      sender.spec(),
      sender.transferRequestTo(receiver, DepositLimits.ABOVE_TRANSFER_MAX));

    sender.assertBalance(fundedBalance);
    receiver.assertBalance(0);
  }
}
