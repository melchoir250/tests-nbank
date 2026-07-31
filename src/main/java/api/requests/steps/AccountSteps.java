package api.requests.steps;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import api.dao.AccountDao;
import api.models.CreateAccountResponse;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.requesters.ApiRequester;
import api.specs.ResponseSpecs;
import io.restassured.specification.RequestSpecification;

public final class AccountSteps {
  private AccountSteps() {
  }

  public static CreateAccountResponse createAccount(RequestSpecification userSpec) {
    return new ApiRequester(userSpec, Endpoint.CREATE_ACCOUNT, ResponseSpecs.entityWasCreated())
        .postAndExtract();
  }

  public static CreateAccountResponse createAccountWithZeroBalance(RequestSpecification userSpec) {
    CreateAccountResponse account = createAccount(userSpec);
    assertAccountBalance(userSpec, account.getId(), 0);
    return account;
  }

  public static CreateAccountResponse[] getAccounts(RequestSpecification userSpec) {
    return new ApiRequester(userSpec, Endpoint.CUSTOMER_ACCOUNTS, ResponseSpecs.requestReturnsOK())
        .get()
        .extract()
        .as(CreateAccountResponse[].class);
  }

  public static List<CreateAccountResponse> getAllAccounts(RequestSpecification userSpec) {
    return Arrays.asList(getAccounts(userSpec));
  }

  public static void assertAccountBalance(
      RequestSpecification userSpec,
      int accountId,
      double expectedBalance) {
    AccountDao accountDao = DataBaseSteps.getAccountById(accountId);
    Assertions.assertThat(accountDao)
        .as("Account %s should exist in database", accountId)
        .isNotNull();
    Assertions.assertThat(accountDao.getBalance())
        .as("Balance of account %s in database", accountId)
        .isCloseTo(expectedBalance, Offset.offset(0.001));
  }
}
