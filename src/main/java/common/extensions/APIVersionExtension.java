package common.extensions;

import api.configs.Config;
import api.configs.PROPERTY;
import common.annotations.APIVersion;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class APIVersionExtension implements ExecutionCondition {
  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    APIVersion annotation = context.getElement()
        .map(element -> element.getAnnotation(APIVersion.class))
        .orElse(null);
    if (annotation == null) {
      annotation = context.getTestClass()
          .map(clazz -> clazz.getAnnotation(APIVersion.class))
          .orElse(null);
    }
    if (annotation == null) {
      return ConditionEvaluationResult.enabled("No @APIVersion annotation");
    }

    String currentVersion = Config.getProperty(PROPERTY.API_CONTRACT_VERSION);
    if (annotation.value().equals(currentVersion)) {
      return ConditionEvaluationResult.enabled("API version matches " + currentVersion);
    }
    return ConditionEvaluationResult.disabled(
        "Test requires api.contractVersion=" + annotation.value()
            + ", current=" + currentVersion);
  }
}
