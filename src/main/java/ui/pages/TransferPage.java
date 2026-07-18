package ui.pages;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import java.util.Locale;
import lombok.Getter;

@Getter
public class TransferPage extends BasePage<TransferPage> {
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
        accountSelector.selectOptionContainingText(senderAccountNumber);
        recipientAccountInput.setValue(recipientAccountNumber);
        amountInput.setValue(String.format(Locale.US, "%.2f", amount));
        if (confirm) {
            confirmCheck.click();
        }
        sendTransferButton.click();
        return this;
    }

    public TransferPage openTransferAgain() {
        transferAgainTab.click();
        return this;
    }

    public TransferPage searchTransactions(String name) {
        searchNameInput.setValue(name);
        searchButton.click();
        return this;
    }

    public ElementsCollection getTransactions() {
        return $$(".list-group-item");
    }

    public TransferPage repeatTransfer(String senderAccountNumber, double amount) {
        String listAmount = String.format(Locale.US, "%.2f", amount);

        getTransactions()
                .findBy(text("TRANSFER_IN - $" + listAmount))
                .$(Selectors.byText("🔁 Repeat"))
                .click();

        $(".modal-body select").selectOptionContainingText(senderAccountNumber);
        $(".modal-body #confirmCheck").click();
        $(".modal-footer").$(Selectors.byText("🚀 Send Transfer")).click();
        return this;
    }
}
