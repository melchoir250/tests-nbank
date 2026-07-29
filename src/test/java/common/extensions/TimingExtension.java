package common.extensions;

import java.util.Locale;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
  private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(TimingExtension.class);
  private static final String START_NANOS = "startNanos";

  @Override
  public void beforeTestExecution(ExtensionContext context) {
    context.getStore(NAMESPACE).put(START_NANOS, System.nanoTime());
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    long startNanos = context.getStore(NAMESPACE).remove(START_NANOS, Long.class);
    double durationMs = (System.nanoTime() - startNanos) / 1_000_000.0;
    String message = String.format(
        Locale.ROOT,
        "[TIMING] test=%s.%s invocation=\"%s\" duration_ms=%.3f thread=%s",
        context.getRequiredTestClass().getSimpleName(),
        context.getRequiredTestMethod().getName(),
        context.getDisplayName().replace('\n', ' '),
        durationMs,
        Thread.currentThread().getName());
    System.out.println(message);
  }
}
