package api;

import api.dao.AccountDao;
import api.generators.RandomData;
import api.models.fraud.FraudTransferRequest;
import api.models.fraud.FraudTransferResponse;
import api.requests.steps.CustomerContext;
import api.requests.steps.DataBaseSteps;
import api.requests.steps.TransferSteps;
import api.support.AccountAssertions;
import api.support.FraudTransferExpectations;
import common.annotations.APIVersion;
import common.annotations.FraudCheckMock;
import common.extensions.FraudCheckWireMockExtension;
import constants.DepositLimits;
import constants.FraudMessages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

@APIVersion("with_fraud_check")
@ExtendWith(FraudCheckWireMockExtension.class)
@DisplayName("POST /api/v1/accounts/transfer-with-fraud-check")
class TransferWithFraudCheckApiTest extends BaseApiTest {

  @Test
  @FraudCheckMock
  @DisplayName("Одобряет перевод с низким риском между пользователями")
  void shouldApproveTransferBetweenUsers(TestInfo testInfo) {
    double depositAmount = RandomData.depositAmount();
    double transferAmount = RandomData.transferAmount(depositAmount);

    CustomerContext sender = CustomerContext.create()
        .withAccount()
        .withDeposit(depositAmount);
    CustomerContext receiver = CustomerContext.create()
        .withAccount();

    FraudTransferRequest request = sender.fraudTransferRequestTo(receiver, transferAmount);
    FraudTransferResponse response = TransferSteps.transferWithFraudCheck(sender.spec(), request);

    assertTransferResponse(testInfo, response, request);
    AccountAssertions.assertBalance(sender.accountId(), depositAmount - transferAmount);
    AccountAssertions.assertBalance(receiver.accountId(), transferAmount);
  }

  @Test
  @FraudCheckMock
  @DisplayName("Одобряет перевод между своими счетами")
  void shouldApproveTransferBetweenOwnAccounts(TestInfo testInfo) {
    double depositAmount = RandomData.depositAmount();
    double transferAmount = RandomData.transferAmount(depositAmount);

    CustomerContext customer = CustomerContext.create()
        .withAccount()
        .withDeposit(depositAmount)
        .withSecondAccount();

    FraudTransferRequest request = customer.fraudTransferToSecondAccount(transferAmount);
    FraudTransferResponse response = TransferSteps.transferWithFraudCheck(customer.spec(), request);

    assertTransferResponse(testInfo, response, request);
    AccountAssertions.assertBalance(customer.accountId(), depositAmount - transferAmount);
    AccountAssertions.assertBalance(customer.secondAccountId(), transferAmount);
  }

  @Test
  @FraudCheckMock(status = FraudMessages.STATUS_MANUAL_REVIEW, requiresManualReview = true)
  @DisplayName("Требует ручной проверки и не меняет балансы")
  void shouldRequireManualReviewWithoutChangingBalances(TestInfo testInfo) {
    assertPendingTransferDoesNotChangeBalances(testInfo);
  }

  @Test
  @FraudCheckMock(status = FraudMessages.STATUS_VERIFICATION, additionalVerificationRequired = true)
  @DisplayName("Требует дополнительной верификации и не меняет балансы")
  void shouldRequireVerificationWithoutChangingBalances(TestInfo testInfo) {
    assertPendingTransferDoesNotChangeBalances(testInfo);
  }

  @Test
  @FraudCheckMock
  @DisplayName("Одобряет перевод около максимального лимита")
  void shouldApproveTransferNearMaximumLimit(TestInfo testInfo) {
    CustomerContext sender = CustomerContext.create()
        .withAccount()
        .withDeposits(DepositLimits.MAX, 2);
    double fundedBalance = sender.balance();
    CustomerContext receiver = CustomerContext.create()
        .withAccount();

    double transferAmount = DepositLimits.JUST_BELOW_TRANSFER_MAX;
    FraudTransferRequest request = sender.fraudTransferRequestTo(receiver, transferAmount);
    FraudTransferResponse response = TransferSteps.transferWithFraudCheck(sender.spec(), request);

    assertTransferResponse(testInfo, response, request);
    AccountAssertions.assertBalance(sender.accountId(), fundedBalance - transferAmount);
    AccountAssertions.assertBalance(receiver.accountId(), transferAmount);
  }

  private void assertTransferResponse(
      TestInfo testInfo,
      FraudTransferResponse response,
      FraudTransferRequest request) {
    FraudCheckMock mock = FraudTransferExpectations.requireMock(testInfo);

    softly.assertThat(response.getTransactionId()).isPositive();
    softly.assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(FraudTransferExpectations.expected(mock, response, request));
  }

  private void assertPendingTransferDoesNotChangeBalances(TestInfo testInfo) {
    double depositAmount = RandomData.depositAmount();
    double transferAmount = RandomData.transferAmount(depositAmount);

    CustomerContext sender = CustomerContext.create()
        .withAccount()
        .withDeposit(depositAmount);
    CustomerContext receiver = CustomerContext.create()
        .withAccount();
    AccountDao senderBefore = DataBaseSteps.getAccountById(sender.accountId());
    AccountDao receiverBefore = DataBaseSteps.getAccountById(receiver.accountId());

    FraudTransferRequest request = sender.fraudTransferRequestTo(receiver, transferAmount);
    FraudTransferResponse response = TransferSteps.transferWithFraudCheck(sender.spec(), request);

    assertTransferResponse(testInfo, response, request);
    AccountAssertions.assertBalanceUnchanged(senderBefore);
    AccountAssertions.assertBalanceUnchanged(receiverBefore);
  }
}
