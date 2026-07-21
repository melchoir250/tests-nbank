package api.specs;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpecs {
  private ResponseSpecs() {}

  public static final String TRANSFER_SUCCESSFUL = "Transfer successful";
  public static final String PROFILE_UPDATED = "Profile updated successfully";
  public static final String DEPOSIT_MIN_AMOUNT = "Deposit amount must be at least 0.01";
  public static final String DEPOSIT_MAX_AMOUNT = "Deposit amount cannot exceed 5000";
  public static final String UNAUTHORIZED_ACCOUNT = "Unauthorized access to account";

  private static ResponseSpecBuilder defaultResponseBuilder() {
    return new ResponseSpecBuilder();
  }

  public static ResponseSpecification entityWasCreated() {
    return defaultResponseBuilder()
      .expectStatusCode(HttpStatus.SC_CREATED)
      .build();
  }

  public static ResponseSpecification requestReturnsOK() {
    return defaultResponseBuilder()
      .expectStatusCode(HttpStatus.SC_OK)
      .build();
  }

  public static ResponseSpecification requestReturnsOKWithMessage(String message) {
    return defaultResponseBuilder()
      .expectStatusCode(HttpStatus.SC_OK)
      .expectBody("message", Matchers.equalTo(message))
      .build();
  }

  public static ResponseSpecification transferSuccessful() {
    return requestReturnsOKWithMessage(TRANSFER_SUCCESSFUL);
  }

  public static ResponseSpecification profileUpdated() {
    return requestReturnsOKWithMessage(PROFILE_UPDATED);
  }

  public static ResponseSpecification requestReturnsBadRequest() {
    return defaultResponseBuilder()
      .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
      .build();
  }

  public static ResponseSpecification requestReturnsBadRequest(
    String errorKey,
    String errorValue) {
    return defaultResponseBuilder()
      .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
      .expectBody(errorKey, Matchers.equalTo(errorValue))
      .build();
  }

  public static ResponseSpecification requestReturnsBadRequestWithMessage(String message) {
    return defaultResponseBuilder()
      .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
      .expectBody(Matchers.equalTo(message))
      .build();
  }

  public static ResponseSpecification depositAmountTooLow() {
    return requestReturnsBadRequestWithMessage(DEPOSIT_MIN_AMOUNT);
  }

  public static ResponseSpecification depositAmountTooHigh() {
    return requestReturnsBadRequestWithMessage(DEPOSIT_MAX_AMOUNT);
  }

  public static ResponseSpecification requestReturnsForbiddenWithMessage(String message) {
    return defaultResponseBuilder()
      .expectStatusCode(HttpStatus.SC_FORBIDDEN)
      .expectBody(Matchers.equalTo(message))
      .build();
  }

  public static ResponseSpecification unauthorizedAccountAccess() {
    return requestReturnsForbiddenWithMessage(UNAUTHORIZED_ACCOUNT);
  }

  public static ResponseSpecification requestReturnsUnauthorized() {
    return defaultResponseBuilder()
      .expectStatusCode(HttpStatus.SC_UNAUTHORIZED)
      .build();
  }
}
