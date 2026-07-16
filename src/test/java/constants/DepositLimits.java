package constants;

public final class DepositLimits {
  private DepositLimits() {}

  public static final double MIN = 0.01;
  public static final double MAX = 5000;
  public static final double JUST_BELOW_MAX = 4999.99;

  public static final double NEGATIVE = -10;
  public static final double ZERO = 0;
  public static final double ABOVE_MAX = 5001;

  public static final int NON_EXISTING_ACCOUNT_ID = 9999;
  public static final int UNAUTHORIZED_TEST_ACCOUNT_ID = 1;

  public static final double TRANSFER_MAX = 10000;
  public static final double JUST_BELOW_TRANSFER_MAX = 9999.99;
  public static final double ABOVE_TRANSFER_MAX = 10001;
}
