package ui;

import api.generators.RandomData;
import api.models.CreateAccountResponse;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import constants.DepositLimits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

@DisplayName("UI / Deposit Money")
class DepositAccountUiTest extends BaseUiTest {

    @Test
    @UserSession
    @DisplayName("позитивный депозит requests/ui/deposit_tests")
    void shouldDepositMoneyToAccount() {

        CreateAccountResponse account = SessionStorage.getSteps().createAccountWithZeroBalance();
        double amount = RandomData.depositAmount();

        new UserDashboard().open()
                .openDeposit()
                .deposit(account.getAccountNumber(), amount)
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_DEPOSITED.getMessage());

        SessionStorage.getSteps().assertAccountBalance(account.getId(), amount);
    }

    @Test
    @UserSession
    @DisplayName("депозит сверх лимита requests/ui/deposit_tests")
    void shouldRejectDepositAboveMaximumLimit() {
        CreateAccountResponse account = SessionStorage.getSteps().createAccountWithZeroBalance();

        new UserDashboard().open()
                .openDeposit()
                .deposit(account.getAccountNumber(), DepositLimits.ABOVE_MAX)
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_LIMIT_EXCEEDED.getMessage());

        SessionStorage.getSteps().assertAccountBalance(account.getId(), 0);
    }
}
