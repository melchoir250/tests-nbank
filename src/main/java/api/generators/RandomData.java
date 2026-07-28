package api.generators;

import java.util.concurrent.ThreadLocalRandom;
import com.github.curiousoddman.rgxgen.RgxGen;

public final class RandomData {
  private static final double DEPOSIT_MIN = 0.01;
  private static final double DEPOSIT_MAX = 5000;

  private RandomData() {}

  public static double depositAmount() {
    double value = ThreadLocalRandom.current()
      .nextDouble(DEPOSIT_MIN + 0.01, DEPOSIT_MAX - 0.01);
    return roundCents(value);
  }

  public static double transferAmount(double availableBalance) {
    double upper = Math.min(availableBalance, 9999.98);
    if (upper <= DEPOSIT_MIN + 0.01) {
      return DEPOSIT_MIN;
    }
    double value = ThreadLocalRandom.current()
      .nextDouble(DEPOSIT_MIN + 0.01, upper);
    return roundCents(value);
  }

  public static String validProfileName() {
    return RgxGen.parse("[A-Z][a-z]{2,10} [A-Z][a-z]{2,10}")
      .generate();
  }

  private static double roundCents(double value) {
    return Math.round(value * 100.0) / 100.0;
  }
}
