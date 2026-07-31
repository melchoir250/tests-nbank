package common.annotations;

import constants.FraudMessages;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface FraudCheckMock {
  String status() default FraudMessages.MOCK_STATUS_SUCCESS;

  String decision() default FraudMessages.MOCK_DECISION_APPROVED;

  double riskScore() default 0.2;

  String reason() default FraudMessages.MOCK_REASON_LOW_RISK;

  boolean requiresManualReview() default false;

  boolean additionalVerificationRequired() default false;

  int port() default 8089;

  String endpoint() default "/fraud-check";
}
