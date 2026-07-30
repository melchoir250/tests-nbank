package api;

import api.models.fraud.FraudCheckStatusResponse;
import api.models.fraud.FraudTransferResponse;
import api.requests.steps.CustomerContext;
import api.requests.steps.UserSteps;
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
        CustomerContext sender = CustomerContext.create()
                .withAccount()
                .withDeposit(1000);
        CustomerContext receiver = CustomerContext.create()
                .withAccount();

        FraudTransferResponse transfer = UserSteps.transferWithFraudCheck(
                sender.spec(),
                sender.fraudTransferRequestTo(receiver, 100));

        FraudCheckStatusResponse status = UserSteps.getFraudCheckStatus(
                sender.spec(),
                transfer.getTransactionId());

        softly.assertThat(status.getTransactionId()).isEqualTo(transfer.getTransactionId());
        softly.assertThat(status.getStatus()).isEqualTo(FraudMessages.FRAUD_STATUS_NOT_REQUIRED);
        softly.assertThat(status.getNote()).isEqualTo(FraudMessages.FRAUD_NOTE_NOT_REQUIRED);
    }

    @Test
    @FraudCheckMock
    @DisplayName("Возвращает статус после перевода между своими счетами")
    void shouldReturnStatusAfterTransferBetweenOwnAccounts() {
        CustomerContext customer = CustomerContext.create()
                .withAccount()
                .withDeposit(1000)
                .withSecondAccount();

        FraudTransferResponse transfer = UserSteps.transferWithFraudCheck(
                customer.spec(),
                customer.fraudTransferToSecondAccount(100));

        FraudCheckStatusResponse status = UserSteps.getFraudCheckStatus(
                customer.spec(),
                transfer.getTransactionId());

        softly.assertThat(status.getTransactionId()).isEqualTo(transfer.getTransactionId());
        softly.assertThat(status.getStatus()).isEqualTo(FraudMessages.FRAUD_STATUS_NOT_REQUIRED);
        softly.assertThat(status.getNote()).isEqualTo(FraudMessages.FRAUD_NOTE_NOT_REQUIRED);
    }
}
