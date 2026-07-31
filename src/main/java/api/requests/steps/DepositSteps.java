package api.requests.steps;

import api.configs.Config;
import api.configs.PROPERTY;
import api.dao.AccountDao;
import api.models.BaseModel;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.models.fraud.FraudDepositRequest;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ApiRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import constants.FraudMessages;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public final class DepositSteps {
  private DepositSteps() {
  }

  public static DepositRequest depositRequest(int accountId, double amount) {
    return DepositRequest.builder()
        .id(accountId)
        .balance(amount)
        .build();
  }

  private static boolean isFraudCheckContract() {
    return "with_fraud_check".equals(Config.getProperty(PROPERTY.API_CONTRACT_VERSION));
  }

  private static BaseModel buildDepositRequestBody(int accountId, double amount) {
    if (isFraudCheckContract()) {
      return FraudDepositRequest.builder()
          .accountId(accountId)
          .amount(amount)
          .description(FraudMessages.DEPOSIT_DESCRIPTION)
          .build();
    }
    return depositRequest(accountId, amount);
  }

  public static DepositResponse deposit(RequestSpecification userSpec, DepositRequest request) {
    new ApiRequester(userSpec, Endpoint.DEPOSIT, ResponseSpecs.requestReturnsOK())
        .post(request);
    AccountDao account = DataBaseSteps.getAccountById(request.getId());
    return DepositResponse.builder()
        .id(request.getId())
        .accountNumber(account.getAccountNumber())
        .balance(account.getBalance())
        .build();
  }

  public static DepositResponse deposit(
      RequestSpecification userSpec,
      int accountId,
      double amount) {
    new ApiRequester(userSpec, Endpoint.DEPOSIT, ResponseSpecs.requestReturnsOK())
        .post(buildDepositRequestBody(accountId, amount));
    AccountDao account = DataBaseSteps.getAccountById(accountId);
    return DepositResponse.builder()
        .id(accountId)
        .accountNumber(account.getAccountNumber())
        .balance(account.getBalance())
        .build();
  }

  public static DepositResponse depositAndAssertBalance(
      RequestSpecification userSpec,
      int accountId,
      double amount,
      double expectedBalance) {
    DepositResponse deposit = deposit(userSpec, accountId, amount);
    AccountSteps.assertAccountBalance(userSpec, accountId, expectedBalance);
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
    new ApiRequester(userSpec, Endpoint.DEPOSIT, responseSpec)
        .post(request);
  }
}
