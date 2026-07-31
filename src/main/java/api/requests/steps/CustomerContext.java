package api.requests.steps;

import io.restassured.specification.RequestSpecification;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.models.DepositTransferRequest;
import api.models.UpdateProfileNameRequest;
import api.models.fraud.FraudTransferRequest;
import api.specs.RequestSpecs;

public final class CustomerContext {
  private final CreateUserRequest user;
  private final String authToken;
  private final RequestSpecification spec;
  private CreateAccountResponse account;
  private CreateAccountResponse secondAccount;
  private double balance;

  private CustomerContext(CreateUserRequest user, String authToken) {
    this.user = user;
    this.authToken = authToken;
    this.spec = RequestSpecs.authenticated(authToken);
  }

  public static CustomerContext create() {
    CreateUserRequest user = AdminSteps.createUser();
    String authToken = RequestSpecs.loginAuthHeader(user.getUsername(), user.getPassword());
    return new CustomerContext(user, authToken);
  }

  public CustomerContext withAccount() {
    account = AccountSteps.createAccountWithZeroBalance(spec);
    balance = 0;
    return this;
  }

  public CustomerContext withDeposit(double amount) {
    requireAccount();
    DepositSteps.depositAndAssertBalance(spec, account.getId(), amount, balance + amount);
    balance += amount;
    return this;
  }

  public CustomerContext withDeposits(double chunk, int times) {
    requireAccount();
    balance = DepositSteps.depositTimes(spec, account.getId(), chunk, times);
    return this;
  }

  public CustomerContext withSecondAccount() {
    secondAccount = AccountSteps.createAccountWithZeroBalance(spec);
    return this;
  }

  public CreateUserRequest user() {
    return user;
  }

  public RequestSpecification spec() {
    return spec;
  }

  public CreateAccountResponse account() {
    return requireAccount();
  }

  public int accountId() {
    return requireAccount().getId();
  }

  public double balance() {
    return balance;
  }

  public String username() {
    return user.getUsername();
  }

  public int secondAccountId() {
    return requireSecondAccount().getId();
  }

  public DepositRequest depositRequest(double amount) {
    return DepositSteps.depositRequest(accountId(), amount);
  }

  public DepositTransferRequest transferRequestTo(CustomerContext receiver, double amount) {
    return TransferSteps.transferRequest(accountId(), receiver.accountId(), amount);
  }

  public FraudTransferRequest fraudTransferRequestTo(CustomerContext receiver, double amount) {
    return TransferSteps.fraudTransferRequest(accountId(), receiver.accountId(), amount);
  }

  public FraudTransferRequest fraudTransferToSecondAccount(double amount) {
    return TransferSteps.fraudTransferRequest(accountId(), secondAccountId(), amount);
  }

  public UpdateProfileNameRequest profileNameRequest(String name) {
    return ProfileSteps.updateProfileNameRequest(name);
  }

  public void assertBalance(double expected) {
    AccountSteps.assertAccountBalance(spec, accountId(), expected);
    balance = expected;
  }

  public void assertProfileName(String expectedName) {
    ProfileSteps.assertProfile(spec, username(), expectedName);
  }

  private CreateAccountResponse requireAccount() {
    if (account == null) {
      throw new IllegalStateException("Account is not created. Call withAccount() first.");
    }
    return account;
  }

  private CreateAccountResponse requireSecondAccount() {
    if (secondAccount == null) {
      throw new IllegalStateException("Second account is not created. Call withSecondAccount() first.");
    }
    return secondAccount;
  }
}
