package constants;

public final class ProfileLimits {
  private ProfileLimits() {}

  public static final String VALID_ONE_CHAR = "M P";
  public static final String VALID_MAX_LENGTH = "A".repeat(100) + " " + "B".repeat(99);

  public static final String EMPTY = " ";
  public static final String WITH_DIGIT = "Petrov123";
  public static final String WITH_SPECIAL = "Petrov@";
  public static final String THREE_WORDS = "A B C";
}
