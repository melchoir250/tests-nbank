package api.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import api.dao.AccountDao;
import api.requests.steps.DataBaseSteps;

public final class AccountAssertions {
  private AccountAssertions() {
  }

  public static void assertBalance(int accountId, double expectedBalance) {
    AccountDao accountDao = DataBaseSteps.getAccountById(accountId);
    assertThat(accountDao).isNotNull();
    assertThat(accountDao.getBalance()).isCloseTo(expectedBalance, offset(0.001));
  }

  public static void assertBalanceUnchanged(AccountDao accountBefore) {
    AccountDao accountAfter = DataBaseSteps.getAccountById(accountBefore.getId());
    assertThat(accountAfter.getBalance())
        .isCloseTo(accountBefore.getBalance(), offset(0.001));
  }
}
