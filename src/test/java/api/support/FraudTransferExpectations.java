package api.support;

import api.models.fraud.FraudTransferRequest;
import api.models.fraud.FraudTransferResponse;
import common.annotations.FraudCheckMock;
import constants.FraudMessages;
import org.junit.jupiter.api.TestInfo;

public final class FraudTransferExpectations {
  private FraudTransferExpectations() {
  }

  public static FraudCheckMock requireMock(TestInfo testInfo) {
    return testInfo.getTestMethod()
        .map(method -> method.getAnnotation(FraudCheckMock.class))
        .orElseThrow(() -> new IllegalStateException(
            "Test method must be annotated with @FraudCheckMock"));
  }

  public static FraudTransferResponse expected(
      FraudCheckMock mock,
      FraudTransferResponse actual,
      FraudTransferRequest request) {
    return FraudTransferResponse.builder()
        .status(resolveStatus(mock))
        .message(resolveMessage(mock))
        .transactionId(actual.getTransactionId())
        .senderAccountId(request.getSenderAccountId())
        .receiverAccountId(request.getReceiverAccountId())
        .amount(request.getAmount())
        .fraudRiskScore(mock.riskScore())
        .fraudReason(mock.reason())
        .requiresManualReview(mock.requiresManualReview())
        .requiresVerification(mock.additionalVerificationRequired())
        .build();
  }

  private static String resolveStatus(FraudCheckMock mock) {
    if (mock.requiresManualReview() || mock.additionalVerificationRequired()) {
      return mock.status();
    }
    return mock.decision();
  }

  private static String resolveMessage(FraudCheckMock mock) {
    if (mock.requiresManualReview()) {
      return FraudMessages.MANUAL_REVIEW;
    }
    if (mock.additionalVerificationRequired()) {
      return FraudMessages.VERIFICATION_REQUIRED;
    }
    return FraudMessages.APPROVED;
  }
}
