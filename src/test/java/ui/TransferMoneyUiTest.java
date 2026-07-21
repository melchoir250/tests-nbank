package ui;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.text;

import api.generators.RandomData;
import api.models.CreateAccountResponse;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import constants.DepositLimits;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.TransactionType;
import ui.pages.UserDashboard;

@DisplayName("UI / Make a Transfer")
class TransferMoneyUiTest extends BaseUiTest {

    @Test
    @UserSession(2)
    @DisplayName("позитивный перевод requests/ui/transfer_tests")
    void shouldTransferMoneyBetweenAccounts() {
        double depositAmount = RandomData.depositAmount();
        double transferAmount = RandomData.transferAmount(depositAmount);

        UserSteps sender = SessionStorage.getSteps(1);
        UserSteps receiver = SessionStorage.getSteps(2);
        CreateAccountResponse from = sender.createAccountWithDeposit(depositAmount);
        CreateAccountResponse to = receiver.createAccountWithZeroBalance();

        new UserDashboard().open()
                .openTransfer()
                .transfer(from.getAccountNumber(), to.getAccountNumber(), transferAmount)
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_TRANSFERRED.getMessage());

        sender.assertAccountBalance(from.getId(), depositAmount - transferAmount);
        receiver.assertAccountBalance(to.getId(), transferAmount);
    }

    @Test
    @UserSession(2)
    @DisplayName("перевод сверх лимита requests/ui/transfer_tests")
    void shouldRejectTransferAboveMaximumLimit() {
        double startBalance = DepositLimits.MAX * 3;

        UserSteps sender = SessionStorage.getSteps(1);
        UserSteps receiver = SessionStorage.getSteps(2);
        CreateAccountResponse from = sender.createAccountWithDeposits(DepositLimits.MAX, 3);
        CreateAccountResponse to = receiver.createAccountWithZeroBalance();

        new UserDashboard().open()
                .openTransfer()
                .transfer(from.getAccountNumber(), to.getAccountNumber(), DepositLimits.ABOVE_TRANSFER_MAX)
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_LIMIT_EXCEEDED.getMessage());

        sender.assertAccountBalance(from.getId(), startBalance);
        receiver.assertAccountBalance(to.getId(), 0);
    }

    @Test
    @UserSession(2)
    @DisplayName("перевод без Confirm requests/ui/transfer_tests")
    void shouldRejectTransferWithoutConfirm() {
        double depositAmount = RandomData.depositAmount();
        double transferAmount = RandomData.transferAmount(depositAmount);

        UserSteps sender = SessionStorage.getSteps(1);
        UserSteps receiver = SessionStorage.getSteps(2);
        CreateAccountResponse from = sender.createAccountWithDeposit(depositAmount);
        CreateAccountResponse to = receiver.createAccountWithZeroBalance();

        new UserDashboard().open()
                .openTransfer()
                .transfer(from.getAccountNumber(), to.getAccountNumber(), transferAmount, false)
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_CONFIRM_REQUIRED.getMessage());

        sender.assertAccountBalance(from.getId(), depositAmount);
        receiver.assertAccountBalance(to.getId(), 0);
    }

    @Test
    @UserSession(2)
    @DisplayName("повторный перевод Transfer Again requests/ui/transfer_tests")
    void shouldRepeatTransferAgain() {
        double depositAmount = RandomData.depositAmount();
        double transferAmount = RandomData.transferAmount(depositAmount / 2);

        UserSteps sender = SessionStorage.getSteps(1);
        UserSteps receiver = SessionStorage.getSteps(2);
        CreateAccountResponse from = sender.createAccountWithDeposit(depositAmount);
        CreateAccountResponse to = receiver.createAccountWithZeroBalance();

        sender.transfer(from.getId(), to.getId(), transferAmount);
        sender.assertAccountBalance(from.getId(), depositAmount - transferAmount);
        receiver.assertAccountBalance(to.getId(), transferAmount);

        new UserDashboard().open()
                .openTransfer()
                .openTransferAgain()
                .searchTransactions(SessionStorage.getUser(2).getUsername())
                .repeatTransfer(from.getAccountNumber(), transferAmount)
                .checkAlertMessageAndAccept(BankAlert.TRANSFER_AGAIN_SUCCESSFUL.getMessage())
                .openDashboard()
                .openTransfer()
                .openTransferAgain()
                .searchTransactions(SessionStorage.getUser(1).getUsername())
                .getTransactions()
                .filterBy(text(TransactionType.TRANSFER_OUT.withAmount(transferAmount)))
                .shouldHave(sizeGreaterThanOrEqual(2));

        sender.assertAccountBalance(from.getId(), depositAmount - transferAmount * 2);
        receiver.assertAccountBalance(to.getId(), transferAmount * 2);
    }
}
