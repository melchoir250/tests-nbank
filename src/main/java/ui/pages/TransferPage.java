package ui.pages;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.Wait;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import java.util.Locale;
import lombok.Getter;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

@Getter
public class TransferPage extends BasePage<TransferPage> {
    private static final String NO_MATCHING_USERS = "No matching users found";

    private SelenideElement accountSelector = $(".account-selector");
    private SelenideElement recipientAccountInput = $(
            Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement confirmCheck = $("#confirmCheck");
    private SelenideElement sendTransferButton = $(Selectors.byText("🚀 Send Transfer"));
    private SelenideElement transferAgainTab = $(Selectors.byText("🔁 Transfer Again"));
    private SelenideElement searchNameInput = $(
            Selectors.byAttribute("placeholder", "Enter name to find transactions"));
    private SelenideElement searchButton = $(Selectors.byText("🔍 Search Transactions"));

    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage transfer(
            String senderAccountNumber,
            String recipientAccountNumber,
            double amount) {
        return transfer(senderAccountNumber, recipientAccountNumber, amount, true);
    }

    public TransferPage transfer(
            String senderAccountNumber,
            String recipientAccountNumber,
            double amount,
            boolean confirm) {
        accountSelector.shouldBe(visible, enabled).selectOptionContainingText(senderAccountNumber);
        recipientAccountInput.shouldBe(visible, enabled).setValue(recipientAccountNumber);
        amountInput.shouldBe(visible, enabled).setValue(String.format(Locale.US, "%.2f", amount));
        if (confirm) {
            confirmCheck.shouldBe(visible, enabled).click();
        }
        sendTransferButton.shouldBe(visible, enabled).click();
        return this;
    }

    public TransferPage openTransferAgain() {
        transferAgainTab.shouldBe(visible, enabled).click();
        return this;
    }

    public TransferPage searchTransactions(String name) {
        searchNameInput.shouldBe(visible, enabled).setValue(name);
        Wait().until(driver -> {
            searchButton.shouldBe(visible, enabled).click();
            try {
                Alert alert = driver.switchTo().alert();
                String message = alert.getText();
                alert.accept();
                if (!message.contains(NO_MATCHING_USERS)) {
                    throw new AssertionError("Unexpected search alert: " + message);
                }
                return false;
            } catch (NoAlertPresentException ignored) {
                return true;
            }
        });
        return this;
    }

    public ElementsCollection getTransactions() {
        return $$(".list-group-item");
    }

    public TransferPage repeatTransfer(String senderAccountNumber, double amount) {
        SelenideElement transaction = getTransactions()
                .findBy(text(TransactionType.TRANSFER_IN.withAmount(amount)))
                .shouldBe(visible);

        transaction
                .$(Selectors.byText("🔁 Repeat"))
                .shouldBe(visible, enabled)
                .click();

        SelenideElement modalBody = $(".modal-body").shouldBe(visible);
        modalBody.$("select").shouldBe(visible, enabled)
                .selectOptionContainingText(senderAccountNumber);
        modalBody.$("#confirmCheck").shouldBe(visible, enabled).click();
        $(".modal-footer").shouldBe(visible)
                .$(Selectors.byText("🚀 Send Transfer"))
                .shouldBe(visible, enabled)
                .click();
        return this;
    }

    public UserDashboard openDashboard() {
        return new UserDashboard().open();
    }
}
