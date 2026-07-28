package ui.pages;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import java.util.Locale;
import lombok.Getter;

@Getter
public class DepositPage extends BasePage<DepositPage> {
    private SelenideElement accountSelector = $(".account-selector");
    private SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement depositButton = $(Selectors.byText("💵 Deposit"));

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage deposit(String accountNumber, double amount) {
        accountSelector.selectOptionContainingText(accountNumber);
        amountInput.setValue(String.format(Locale.US, "%.2f", amount));
        depositButton.click();
        return this;
    }
}
