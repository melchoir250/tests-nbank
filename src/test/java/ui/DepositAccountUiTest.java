package ui;

import api.generators.RandomData;
import api.requests.steps.CustomerContext;
import constants.DepositLimits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

@DisplayName("UI / Deposit Money")
class DepositAccountUiTest extends BaseUiTest {

    @Test
    @DisplayName("позитивный депозит requests/ui/deposit_tests")
    void shouldDepositMoneyToAccount() {
        CustomerContext customer = CustomerContext.create().withAccount();
        double amount = RandomData.depositAmount();

        authAsUser(customer.user());
        new UserDashboard().open()
                .openDeposit()
                .deposit(customer.account().getAccountNumber(), amount)
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_DEPOSITED.getMessage());
        customer.assertBalance(amount);
    }

    @Test
    @DisplayName("депозит сверх лимита requests/ui/deposit_tests")
    void shouldRejectDepositAboveMaximumLimit() {
        CustomerContext customer = CustomerContext.create().withAccount();

        authAsUser(customer.user());
        new UserDashboard().open()
                .openDeposit()
                .deposit(customer.account().getAccountNumber(), DepositLimits.ABOVE_MAX)
                .checkAlertMessageAndAccept(BankAlert.DEPOSIT_LIMIT_EXCEEDED.getMessage());
        customer.assertBalance(0);
    }
}
