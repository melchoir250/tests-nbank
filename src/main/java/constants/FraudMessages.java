package constants;

public final class FraudMessages {
  private FraudMessages() {
  }

  public static final String MOCK_STATUS_SUCCESS = "SUCCESS";
  public static final String MOCK_DECISION_APPROVED = "APPROVED";
  public static final String MOCK_REASON_LOW_RISK = "Low risk transaction";

  public static final String APPROVED = "Transfer approved and processed immediately";
  public static final String MANUAL_REVIEW = "Transfer requires manual review";
  public static final String VERIFICATION_REQUIRED = "Additional verification required";

  public static final String STATUS_APPROVED = "APPROVED";
  public static final String STATUS_MANUAL_REVIEW = "MANUAL_REVIEW_REQUIRED";
  public static final String STATUS_VERIFICATION = "VERIFICATION_REQUIRED";

  public static final String FRAUD_STATUS_NOT_REQUIRED = "NO_FRAUD_CHECK_REQUIRED";
  public static final String FRAUD_NOTE_NOT_REQUIRED = "This transaction does not require fraud checking.";

  public static final String TRANSFER_DESCRIPTION = "Test transfer with fraud check";
  public static final String DEPOSIT_DESCRIPTION = "Test deposit";
}
