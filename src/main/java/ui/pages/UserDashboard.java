package ui.pages;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import java.util.Locale;
import lombok.Getter;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
  private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
  private SelenideElement createNewAccount = $(Selectors.byText("➕ Create New Account"));
  private SelenideElement depositMoney = $(Selectors.byText("💰 Deposit Money"));

  @Override
  public String url() {
    return "/dashboard";
  }

  public UserDashboard createNewAccount() {
    createNewAccount.click();
    return this;
  }

  public UserDashboard deposit(String accountNumber, double amount) {
    depositMoney.shouldBe(visible).click();
    $(".account-selector").shouldBe(visible)
        .selectOptionContainingText(accountNumber);
    $(Selectors.byAttribute("placeholder", "Enter amount"))
        .shouldBe(visible)
        .setValue(String.format(Locale.US, "%.2f", amount));
    $(Selectors.byText("💵 Deposit")).shouldBe(visible).shouldBe(enabled).click();
    return this;
  }
}
