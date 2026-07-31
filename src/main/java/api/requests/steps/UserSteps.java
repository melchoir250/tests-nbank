package api.requests.steps;

import java.util.List;
import api.models.CreateAccountResponse;
import api.models.DepositTransferResponse;
import api.specs.RequestSpecs;
import io.restassured.specification.RequestSpecification;

public final class UserSteps {
  private final String username;
  private final RequestSpecification spec;

  public UserSteps(String username, String password) {
    this.username = username;
    this.spec = RequestSpecs.authAsUser(username, password);
  }

  public RequestSpecification spec() {
    return spec;
  }

  public List<CreateAccountResponse> getAllAccounts() {
    return AccountSteps.getAllAccounts(spec);
  }

  public CreateAccountResponse createAccountWithZeroBalance() {
    return AccountSteps.createAccountWithZeroBalance(spec);
  }

  public CreateAccountResponse createAccountWithDeposit(double amount) {
    CreateAccountResponse account = createAccountWithZeroBalance();
    DepositSteps.depositAndAssertBalance(spec, account.getId(), amount, amount);
    return account;
  }

  public CreateAccountResponse createAccountWithDeposits(double chunk, int times) {
    CreateAccountResponse account = createAccountWithZeroBalance();
    DepositSteps.depositTimes(spec, account.getId(), chunk, times);
    return account;
  }

  public DepositTransferResponse transfer(int senderAccountId, int receiverAccountId, double amount) {
    return TransferSteps.transfer(
        spec,
        TransferSteps.transferRequest(senderAccountId, receiverAccountId, amount));
  }

  public void assertAccountBalance(int accountId, double expectedBalance) {
    AccountSteps.assertAccountBalance(spec, accountId, expectedBalance);
  }

  public void assertProfileName(String expectedName) {
    ProfileSteps.assertProfile(spec, username, expectedName);
  }

  public void updateProfileName(String name) {
    ProfileSteps.updateProfileName(spec, ProfileSteps.updateProfileNameRequest(name));
  }
}
