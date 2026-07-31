package api;

import api.generators.RandomData;
import api.models.fraud.FraudCheckStatusResponse;
import api.models.fraud.FraudTransferRequest;
import api.models.fraud.FraudTransferResponse;
import api.requests.steps.CustomerContext;
import api.requests.steps.TransferSteps;
import common.annotations.APIVersion;
import common.annotations.FraudCheckMock;
import common.extensions.FraudCheckWireMockExtension;
import constants.FraudMessages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@APIVersion("with_fraud_check")
@ExtendWith(FraudCheckWireMockExtension.class)
@DisplayName("GET /api/v1/accounts/fraud-check/{id}")
class GetFraudCheckStatusApiTest extends BaseApiTest {

  @Test
  @FraudCheckMock
  @DisplayName("Возвращает статус после перевода другому пользователю")
  void shouldReturnStatusAfterTransferToAnotherUser() {
    double depositAmount = RandomData.depositAmount();
    double transferAmount = RandomData.transferAmount(depositAmount);

    CustomerContext sender = CustomerContext.create()
        .withAccount()
        .withDeposit(depositAmount);
    CustomerContext receiver = CustomerContext.create()
        .withAccount();

    assertFraudStatusAfterTransfer(
        sender,
        sender.fraudTransferRequestTo(receiver, transferAmount));
  }

  @Test
  @FraudCheckMock
  @DisplayName("Возвращает статус после перевода между своими счетами")
  void shouldReturnStatusAfterTransferBetweenOwnAccounts() {
    double depositAmount = RandomData.depositAmount();
    double transferAmount = RandomData.transferAmount(depositAmount);

    CustomerContext customer = CustomerContext.create()
        .withAccount()
        .withDeposit(depositAmount)
        .withSecondAccount();

    assertFraudStatusAfterTransfer(
        customer,
        customer.fraudTransferToSecondAccount(transferAmount));
  }

  private void assertFraudStatusAfterTransfer(
      CustomerContext performer,
      FraudTransferRequest request) {
    FraudTransferResponse transfer = TransferSteps.transferWithFraudCheck(performer.spec(), request);
    FraudCheckStatusResponse status = TransferSteps.getFraudCheckStatus(
        performer.spec(),
        transfer.getTransactionId());

    softly.assertThat(status.getTransactionId()).isEqualTo(transfer.getTransactionId());
    softly.assertThat(status.getStatus()).isEqualTo(FraudMessages.FRAUD_STATUS_NOT_REQUIRED);
    softly.assertThat(status.getNote()).isEqualTo(FraudMessages.FRAUD_NOTE_NOT_REQUIRED);
  }
}
