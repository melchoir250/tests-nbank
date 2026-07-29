package api.specs;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class ResponseSpecs {
  private ResponseSpecs() {
  }

  public static final String TRANSFER_SUCCESSFUL = "Transfer successful";
  public static final String PROFILE_UPDATED = "Profile updated successfully";
  public static final String DEPOSIT_MIN_AMOUNT = "Deposit amount must be at least 0.01";
  public static final String DEPOSIT_MAX_AMOUNT = "Deposit amount cannot exceed 5000";
  public static final String TRANSFER_MIN_AMOUNT = "Transfer amount must be at least 0.01";
  public static final String TRANSFER_MAX_AMOUNT = "Transfer amount cannot exceed 10000";
  public static final String INVALID_PROFILE_NAME = "Name must contain two words with letters only";
  public static final String USERNAME_REQUIRED = "Username cannot be blank";
  public static final String USERNAME_SIZE = "Username must be between 3 and 15 characters";
  public static final String USERNAME_PATTERN = "Username must contain only letters, digits, dashes, underscores, and dots";
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

  public static ResponseSpecification requestReturnsBadRequestWithErrors(
      String errorKey,
      String... errorValues) {
    return defaultResponseBuilder()
        .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
        .expectBody(errorKey, Matchers.containsInAnyOrder(errorValues))
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

  public static ResponseSpecification transferAmountTooLow() {
    return requestReturnsBadRequestWithMessage(TRANSFER_MIN_AMOUNT);
  }

  public static ResponseSpecification transferAmountTooHigh() {
    return requestReturnsBadRequestWithMessage(TRANSFER_MAX_AMOUNT);
  }

  public static ResponseSpecification invalidProfileName() {
    return requestReturnsBadRequestWithMessage(INVALID_PROFILE_NAME);
  }

  public static ResponseSpecification invalidUsernameErrors() {
    return requestReturnsBadRequestWithErrors(
        "username",
        USERNAME_REQUIRED,
        USERNAME_SIZE,
        USERNAME_PATTERN);
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
