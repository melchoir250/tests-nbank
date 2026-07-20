package api.requests.steps;

import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.CreateAccountResponse;
import api.models.CustomerProfile;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.models.DepositTransferRequest;
import api.models.DepositTransferResponse;
import api.models.UpdateProfileNameRequest;
import api.models.UpdateProfileNameResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.CrudRequester;
import api.requests.skelethon.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public final class UserSteps {
  private UserSteps() {
  }

  public static CreateAccountResponse createAccount(RequestSpecification userSpec) {
    return new ValidatedCrudRequester<CreateAccountResponse>(
        userSpec,
        Endpoint.CREATE_ACCOUNT,
        ResponseSpecs.entityWasCreated())
        .post();
  }

  public static CreateAccountResponse createAccountWithZeroBalance(RequestSpecification userSpec) {
    CreateAccountResponse account = createAccount(userSpec);
    assertAccountBalance(userSpec, account.getId(), 0);
    return account;
  }

  public static DepositRequest depositRequest(int accountId, double amount) {
    return DepositRequest.builder()
        .id(accountId)
        .balance(amount)
        .build();
  }

  public static DepositResponse deposit(RequestSpecification userSpec, DepositRequest request) {
    return new ValidatedCrudRequester<DepositResponse>(
        userSpec,
        Endpoint.DEPOSIT,
        ResponseSpecs.requestReturnsOK())
        .post(request);
  }

  public static DepositResponse deposit(
      RequestSpecification userSpec,
      int accountId,
      double amount) {
    return deposit(userSpec, depositRequest(accountId, amount));
  }

  public static DepositResponse depositAndAssertBalance(
      RequestSpecification userSpec,
      int accountId,
      double amount,
      double expectedBalance) {
    DepositResponse deposit = deposit(userSpec, accountId, amount);
    assertAccountBalance(userSpec, accountId, expectedBalance);
    return deposit;
  }

  public static double depositTimes(
      RequestSpecification userSpec,
      int accountId,
      double amount,
      int times) {
    double balance = 0;
    for (int i = 0; i < times; i++) {
      balance += amount;
      depositAndAssertBalance(userSpec, accountId, amount, balance);
    }
    return balance;
  }

  public static void depositExpectingMinAmountError(
      RequestSpecification userSpec,
      DepositRequest request) {
    depositExpecting(userSpec, request, ResponseSpecs.depositAmountTooLow());
  }

  public static void depositExpectingMaxAmountError(
      RequestSpecification userSpec,
      DepositRequest request) {
    depositExpecting(userSpec, request, ResponseSpecs.depositAmountTooHigh());
  }

  public static void depositExpectingForbidden(
      RequestSpecification userSpec,
      DepositRequest request) {
    depositExpecting(userSpec, request, ResponseSpecs.unauthorizedAccountAccess());
  }

  public static void depositExpectingUnauthorized(DepositRequest request) {
    depositExpecting(
        RequestSpecs.unauthSpec(),
        request,
        ResponseSpecs.requestReturnsUnauthorized());
  }

  private static void depositExpecting(
      RequestSpecification userSpec,
      DepositRequest request,
      ResponseSpecification responseSpec) {
    new CrudRequester(userSpec, Endpoint.DEPOSIT, responseSpec)
        .post(request);
  }

  public static DepositTransferRequest transferRequest(
      int senderAccountId,
      int receiverAccountId,
      double amount) {
    return DepositTransferRequest.builder()
        .senderAccountId(senderAccountId)
        .receiverAccountId(receiverAccountId)
        .amount(amount)
        .build();
  }

  public static DepositTransferResponse transfer(
      RequestSpecification userSpec,
      DepositTransferRequest request) {
    return new ValidatedCrudRequester<DepositTransferResponse>(
        userSpec,
        Endpoint.TRANSFER,
        ResponseSpecs.transferSuccessful())
        .post(request);
  }

  public static void transferExpectingBadRequest(
      RequestSpecification userSpec,
      DepositTransferRequest request) {
    new CrudRequester(
        userSpec,
        Endpoint.TRANSFER,
        ResponseSpecs.requestReturnsBadRequest())
        .post(request);
  }

  public static CreateAccountResponse[] getAccounts(RequestSpecification userSpec) {
    return new CrudRequester(
        userSpec,
        Endpoint.CUSTOMER_ACCOUNTS,
        ResponseSpecs.requestReturnsOK())
        .get()
        .extract()
        .as(CreateAccountResponse[].class);
  }

  public static void assertAccountBalance(
      RequestSpecification userSpec,
      int accountId,
      double expectedBalance) {
    double actualBalance = Arrays.stream(getAccounts(userSpec))
        .filter(account -> account.getId() == accountId)
        .findFirst()
        .orElseThrow(() -> new AssertionError("Account not found: " + accountId))
        .getBalance();

    Assertions.assertThat(actualBalance)
        .as("Balance of account %s via GET /customer/accounts", accountId)
        .isCloseTo(expectedBalance, Offset.offset(0.001));
  }

  public static CustomerProfile getProfile(RequestSpecification userSpec) {
    return new ValidatedCrudRequester<CustomerProfile>(
        userSpec,
        Endpoint.GET_PROFILE,
        ResponseSpecs.requestReturnsOK())
        .get();
  }

  public static UpdateProfileNameRequest updateProfileNameRequest(String name) {
    return UpdateProfileNameRequest.builder()
        .name(name)
        .build();
  }

  public static UpdateProfileNameResponse updateProfileName(
      RequestSpecification userSpec,
      UpdateProfileNameRequest request) {
    return new ValidatedCrudRequester<UpdateProfileNameResponse>(
        userSpec,
        Endpoint.UPDATE_PROFILE,
        ResponseSpecs.profileUpdated())
        .put(request);
  }

  public static void updateProfileNameExpectingBadRequest(
      RequestSpecification userSpec,
      UpdateProfileNameRequest request) {
    new CrudRequester(
        userSpec,
        Endpoint.UPDATE_PROFILE,
        ResponseSpecs.requestReturnsBadRequest())
        .put(request);
  }

  public static void assertProfile(
      RequestSpecification userSpec,
      String expectedUsername,
      String expectedName) {
    CustomerProfile profile = getProfile(userSpec);

    Assertions.assertThat(profile.getUsername())
        .as("Profile username via GET /customer/profile")
        .isEqualTo(expectedUsername);
    Assertions.assertThat(profile.getName())
        .as("Profile name via GET /customer/profile")
        .isEqualTo(expectedName);
  }
}
