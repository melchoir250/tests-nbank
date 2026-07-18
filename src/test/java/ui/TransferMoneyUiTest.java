package ui;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.text;

import api.generators.RandomData;
import api.requests.steps.CustomerContext;
import api.requests.steps.UserSteps;
import constants.DepositLimits;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

@DisplayName("UI / Make a Transfer")
class TransferMoneyUiTest extends BaseUiTest {

    @Test
    @DisplayName("позитивный перевод requests/ui/transfer_tests")
    void shouldTransferMoneyBetweenAccounts() {
        double depositAmount = RandomData.depositAmount();
        double transferAmount = RandomData.transferAmount(depositAmount);
        CustomerContext user1 = CustomerContext.create()
                .withAccount()
                .withDeposit(depositAmount);
        CustomerContext user2 = CustomerContext.create()
                .withAccount();

        authAsUser(user1.user());
        new UserDashboard().open()
                .openTransfer()
                .transfer(
                        user1.account().getAccountNumber(),
                        user2.account().getAccountNumber(),
                        transferAmount)
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_TRANSFERRED.getMessage());

        user1.assertBalance(depositAmount - transferAmount);
        user2.assertBalance(transferAmount);
    }

    @Test
    @DisplayName("перевод сверх лимита requests/ui/transfer_tests")
    void shouldRejectTransferAboveMaximumLimit() {
        double startBalance = 15_000;
        CustomerContext user1 = CustomerContext.create()
                .withAccount()
                .withDeposits(DepositLimits.MAX, 3);
        CustomerContext user2 = CustomerContext.create()
                .withAccount();

        authAsUser(user1.user());
        new UserDashboard().open()
                .openTransfer()
                .transfer(
                        user1.account().getAccountNumber(),
                        user2.account().getAccountNumber(),
                        DepositLimits.ABOVE_TRANSFER_MAX)
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_LIMIT_EXCEEDED.getMessage());

        user1.assertBalance(startBalance);
        user2.assertBalance(0);
    }

    @Test
    @DisplayName("перевод без Confirm requests/ui/transfer_tests")
    void shouldRejectTransferWithoutConfirm() {
        double depositAmount = RandomData.depositAmount();
        double transferAmount = RandomData.transferAmount(depositAmount);
        CustomerContext user1 = CustomerContext.create()
                .withAccount()
                .withDeposit(depositAmount);
        CustomerContext user2 = CustomerContext.create()
                .withAccount();

        authAsUser(user1.user());
        new UserDashboard().open()
                .openTransfer()
                .transfer(
                        user1.account().getAccountNumber(),
                        user2.account().getAccountNumber(),
                        transferAmount,
                        false)
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_CONFIRM_REQUIRED.getMessage());

        user1.assertBalance(depositAmount);
        user2.assertBalance(0);
    }

    @Test
    @DisplayName("повторный перевод Transfer Again requests/ui/transfer_tests")
    void shouldRepeatTransferAgain() {
        double depositAmount = 1000;
        double transferAmount = 50;
        CustomerContext user1 = CustomerContext.create()
                .withAccount()
                .withDeposit(depositAmount);
        CustomerContext user2 = CustomerContext.create()
                .withAccount();

        UserSteps.transfer(user1.spec(), user1.transferRequestTo(user2, transferAmount));
        user1.assertBalance(depositAmount - transferAmount);
        user2.assertBalance(transferAmount);

        authAsUser(user1.user());
        new UserDashboard().open()
                .openTransfer()
                .openTransferAgain()
                .searchTransactions(user2.username())
                .repeatTransfer(user1.account().getAccountNumber(), transferAmount)
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_AGAIN_SUCCESSFUL.getMessage());

        new UserDashboard().open()
                .openTransfer()
                .openTransferAgain()
                .searchTransactions(user1.username())
                .getTransactions()
                .filterBy(text("TRANSFER_OUT - $" + String.format(Locale.US, "%.2f", transferAmount)))
                .shouldHave(sizeGreaterThanOrEqual(2));

        user1.assertBalance(depositAmount - transferAmount * 2);
        user2.assertBalance(transferAmount * 2);
    }
}
