package api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import api.dao.AccountDao;
import api.generators.RandomData;
import api.models.comparison.ModelAssertions;
import api.models.fraud.FraudTransferRequest;
import api.models.fraud.FraudTransferResponse;
import api.requests.steps.CustomerContext;
import api.requests.steps.DataBaseSteps;
import api.requests.steps.UserSteps;
import common.annotations.APIVersion;
import common.annotations.FraudCheckMock;
import common.extensions.FraudCheckWireMockExtension;
import constants.DepositLimits;
import constants.FraudMessages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@APIVersion("with_fraud_check")
@ExtendWith(FraudCheckWireMockExtension.class)
@DisplayName("POST /api/v1/accounts/transfer-with-fraud-check")
class TransferWithFraudCheckApiTest extends BaseApiTest {

    @Test
    @FraudCheckMock
    @DisplayName("Одобряет перевод с низким риском между пользователями")
    void shouldApproveTransferBetweenUsers() {
        double depositAmount = RandomData.depositAmount();
        double transferAmount = RandomData.transferAmount(depositAmount);

        CustomerContext sender = CustomerContext.create()
                .withAccount()
                .withDeposit(depositAmount);
        CustomerContext receiver = CustomerContext.create()
                .withAccount();

        FraudTransferRequest request = sender.fraudTransferRequestTo(receiver, transferAmount);
        FraudTransferResponse response = UserSteps.transferWithFraudCheck(sender.spec(), request);

        softly.assertThat(response.getTransactionId()).isPositive();
        ModelAssertions.assertThatModels(
                expectedApprovedResponse(response, request),
                response).match();
        assertAccountBalance(sender.accountId(), depositAmount - transferAmount);
        assertAccountBalance(receiver.accountId(), transferAmount);
    }

    @Test
    @FraudCheckMock
    @DisplayName("Одобряет перевод между своими счетами")
    void shouldApproveTransferBetweenOwnAccounts() {
        double depositAmount = RandomData.depositAmount();
        double transferAmount = RandomData.transferAmount(depositAmount);

        CustomerContext customer = CustomerContext.create()
                .withAccount()
                .withDeposit(depositAmount)
                .withSecondAccount();

        FraudTransferRequest request = customer.fraudTransferToSecondAccount(transferAmount);
        FraudTransferResponse response = UserSteps.transferWithFraudCheck(customer.spec(), request);

        softly.assertThat(response.getTransactionId()).isPositive();
        ModelAssertions.assertThatModels(
                expectedApprovedResponse(response, request),
                response).match();
        assertAccountBalance(customer.accountId(), depositAmount - transferAmount);
        assertAccountBalance(customer.secondAccountId(), transferAmount);
    }

    @Test
    @FraudCheckMock(status = FraudMessages.STATUS_MANUAL_REVIEW, requiresManualReview = true)
    @DisplayName("Требует ручной проверки и не меняет балансы")
    void shouldRequireManualReviewWithoutChangingBalances() {
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
        FraudTransferResponse response = UserSteps.transferWithFraudCheck(sender.spec(), request);

        softly.assertThat(response.getTransactionId()).isPositive();
        ModelAssertions.assertThatModels(
                expectedPendingResponse(
                        response,
                        request,
                        FraudMessages.STATUS_MANUAL_REVIEW,
                        FraudMessages.MANUAL_REVIEW,
                        true,
                        false),
                response).match();
        assertBalanceUnchanged(senderBefore);
        assertBalanceUnchanged(receiverBefore);
    }

    @Test
    @FraudCheckMock(status = FraudMessages.STATUS_VERIFICATION, additionalVerificationRequired = true)
    @DisplayName("Требует дополнительной верификации и не меняет балансы")
    void shouldRequireVerificationWithoutChangingBalances() {
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
        FraudTransferResponse response = UserSteps.transferWithFraudCheck(sender.spec(), request);

        softly.assertThat(response.getTransactionId()).isPositive();
        ModelAssertions.assertThatModels(
                expectedPendingResponse(
                        response,
                        request,
                        FraudMessages.STATUS_VERIFICATION,
                        FraudMessages.VERIFICATION_REQUIRED,
                        false,
                        true),
                response).match();
        assertBalanceUnchanged(senderBefore);
        assertBalanceUnchanged(receiverBefore);
    }

    @Test
    @FraudCheckMock
    @DisplayName("Одобряет перевод около максимального лимита")
    void shouldApproveTransferNearMaximumLimit() {
        CustomerContext sender = CustomerContext.create()
                .withAccount()
                .withDeposits(DepositLimits.MAX, 2);
        double fundedBalance = sender.balance();
        CustomerContext receiver = CustomerContext.create()
                .withAccount();

        FraudTransferRequest request = sender.fraudTransferRequestTo(
                receiver,
                DepositLimits.JUST_BELOW_TRANSFER_MAX);
        FraudTransferResponse response = UserSteps.transferWithFraudCheck(sender.spec(), request);

        softly.assertThat(response.getTransactionId()).isPositive();
        ModelAssertions.assertThatModels(
                expectedApprovedResponse(response, request),
                response).match();
        assertAccountBalance(sender.accountId(), fundedBalance - DepositLimits.JUST_BELOW_TRANSFER_MAX);
        assertAccountBalance(receiver.accountId(), DepositLimits.JUST_BELOW_TRANSFER_MAX);
    }

    private static FraudTransferResponse expectedApprovedResponse(
            FraudTransferResponse response,
            FraudTransferRequest request) {
        return FraudTransferResponse.builder()
                .status(FraudMessages.STATUS_APPROVED)
                .message(FraudMessages.APPROVED)
                .transactionId(response.getTransactionId())
                .senderAccountId(request.getSenderAccountId())
                .receiverAccountId(request.getReceiverAccountId())
                .amount(request.getAmount())
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresVerification(false)
                .requiresManualReview(false)
                .build();
    }

    private static FraudTransferResponse expectedPendingResponse(
            FraudTransferResponse response,
            FraudTransferRequest request,
            String status,
            String message,
            boolean requiresManualReview,
            boolean requiresVerification) {
        return FraudTransferResponse.builder()
                .status(status)
                .message(message)
                .transactionId(response.getTransactionId())
                .senderAccountId(request.getSenderAccountId())
                .receiverAccountId(request.getReceiverAccountId())
                .amount(request.getAmount())
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresVerification(requiresVerification)
                .requiresManualReview(requiresManualReview)
                .build();
    }

    private static void assertAccountBalance(int accountId, double expectedBalance) {
        AccountDao accountDao = DataBaseSteps.getAccountById(accountId);
        assertThat(accountDao).isNotNull();
        assertThat(accountDao.getBalance()).isCloseTo(expectedBalance, offset(0.001));
    }

    private static void assertBalanceUnchanged(AccountDao accountBefore) {
        AccountDao accountAfter = DataBaseSteps.getAccountById(accountBefore.getId());
        assertThat(accountAfter.getBalance())
                .isCloseTo(accountBefore.getBalance(), offset(0.001));
    }
}
