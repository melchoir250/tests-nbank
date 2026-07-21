package api.requests.steps;

import io.restassured.specification.RequestSpecification;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.models.DepositTransferRequest;
import api.models.UpdateProfileNameRequest;
import api.specs.RequestSpecs;

public final class CustomerContext {
  private final CreateUserRequest user;
  private final String authToken;
  private final RequestSpecification spec;
  private CreateAccountResponse account;
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
    account = UserSteps.createAccountWithZeroBalance(spec);
    balance = 0;
    return this;
  }

  public CustomerContext withDeposit(double amount) {
    requireAccount();
    UserSteps.depositAndAssertBalance(spec, account.getId(), amount, balance + amount);
    balance += amount;
    return this;
  }

  public CustomerContext withDeposits(double chunk, int times) {
    requireAccount();
    balance = UserSteps.depositTimes(spec, account.getId(), chunk, times);
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

  public DepositRequest depositRequest(double amount) {
    return UserSteps.depositRequest(accountId(), amount);
  }

  public DepositTransferRequest transferRequestTo(CustomerContext receiver, double amount) {
    return UserSteps.transferRequest(accountId(), receiver.accountId(), amount);
  }

  public UpdateProfileNameRequest profileNameRequest(String name) {
    return UserSteps.updateProfileNameRequest(name);
  }

  public void assertBalance(double expected) {
    UserSteps.assertAccountBalance(spec, accountId(), expected);
    balance = expected;
  }

  public void assertProfileName(String expectedName) {
    UserSteps.assertProfile(spec, username(), expectedName);
  }

  private CreateAccountResponse requireAccount() {
    if (account == null) {
      throw new IllegalStateException("Account is not created. Call withAccount() first.");
    }
    return account;
  }
}
