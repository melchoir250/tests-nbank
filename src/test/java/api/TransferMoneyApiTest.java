package api;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import constants.DepositLimits;
import api.dao.AccountDao;
import api.generators.RandomData;
import api.models.DepositTransferRequest;
import api.models.DepositTransferResponse;
import api.models.comparison.ModelAssertions;
import api.requests.steps.CustomerContext;
import api.requests.steps.DataBaseSteps;
import api.requests.steps.TransferSteps;
import api.support.AccountAssertions;
import common.annotations.APIVersion;

@APIVersion("with_database")
@DisplayName("POST /api/v1/accounts/transfer")
class TransferMoneyApiTest extends BaseApiTest {

  @ParameterizedTest
  @MethodSource("positiveTransferAmounts")
  @DisplayName("Принимает допустимую сумму перевода")
  void shouldAcceptValidTransferAmount(double depositAmount, double transferAmount) {
    CustomerContext sender = CustomerContext.create()
        .withAccount()
        .withDeposit(depositAmount);
    CustomerContext receiver = CustomerContext.create()
        .withAccount();

    DepositTransferRequest transferRequest = sender.transferRequestTo(receiver, transferAmount);
    DepositTransferResponse transfer = TransferSteps.transfer(sender.spec(), transferRequest);

    ModelAssertions.assertThatModels(transferRequest, transfer)
        .match();
    AccountAssertions.assertBalance(sender.accountId(), depositAmount - transferAmount);
    AccountAssertions.assertBalance(receiver.accountId(), transferAmount);
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
  @DisplayName("Принимает перевод около максимального лимита")
  void shouldAcceptTransferNearMaximumLimit(double transferAmount) {
    CustomerContext sender = CustomerContext.create()
        .withAccount()
        .withDeposits(DepositLimits.MAX, 2);
    double fundedBalance = sender.balance();
    CustomerContext receiver = CustomerContext.create()
        .withAccount();

    DepositTransferRequest transferRequest = sender.transferRequestTo(receiver, transferAmount);
    DepositTransferResponse transfer = TransferSteps.transfer(sender.spec(), transferRequest);

    ModelAssertions.assertThatModels(transferRequest, transfer)
        .match();
    AccountAssertions.assertBalance(sender.accountId(), fundedBalance - transferAmount);
    AccountAssertions.assertBalance(receiver.accountId(), transferAmount);
  }

  static Stream<Arguments> transferNearMaxAmounts() {
    return Stream.of(
        Arguments.of(DepositLimits.TRANSFER_MAX),
        Arguments.of(DepositLimits.JUST_BELOW_TRANSFER_MAX));
  }

  @ParameterizedTest
  @MethodSource("invalidTransferAmounts")
  @DisplayName("Отклоняет недопустимую сумму перевода")
  void shouldRejectInvalidTransferAmount(double transferAmount) {
    double depositAmount = RandomData.depositAmount();
    CustomerContext sender = CustomerContext.create()
        .withAccount()
        .withDeposit(depositAmount);
    CustomerContext receiver = CustomerContext.create()
        .withAccount();
    AccountDao senderBefore = DataBaseSteps.getAccountById(sender.accountId());
    AccountDao receiverBefore = DataBaseSteps.getAccountById(receiver.accountId());

    TransferSteps.transferExpectingMinAmountError(
        sender.spec(),
        sender.transferRequestTo(receiver, transferAmount));

    AccountAssertions.assertBalanceUnchanged(senderBefore);
    AccountAssertions.assertBalanceUnchanged(receiverBefore);
  }

  static Stream<Arguments> invalidTransferAmounts() {
    return Stream.of(
        Arguments.of(DepositLimits.ZERO),
        Arguments.of(DepositLimits.NEGATIVE));
  }

  @Test
  @DisplayName("Отклоняет перевод выше максимального лимита")
  void shouldRejectTransferAboveMaximumLimit() {
    CustomerContext sender = CustomerContext.create()
        .withAccount()
        .withDeposits(DepositLimits.MAX, 3);
    CustomerContext receiver = CustomerContext.create()
        .withAccount();
    AccountDao senderBefore = DataBaseSteps.getAccountById(sender.accountId());
    AccountDao receiverBefore = DataBaseSteps.getAccountById(receiver.accountId());

    TransferSteps.transferExpectingMaxAmountError(
        sender.spec(),
        sender.transferRequestTo(receiver, DepositLimits.ABOVE_TRANSFER_MAX));

    AccountAssertions.assertBalanceUnchanged(senderBefore);
    AccountAssertions.assertBalanceUnchanged(receiverBefore);
  }
}
