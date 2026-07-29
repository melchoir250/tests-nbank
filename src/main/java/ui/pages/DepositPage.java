package ui.pages;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
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
        accountSelector.shouldBe(visible, enabled).selectOptionContainingText(accountNumber);
        amountInput.shouldBe(visible, enabled).setValue(String.format(Locale.US, "%.2f", amount));
        depositButton.shouldBe(visible, enabled).click();
        return this;
    }
}
