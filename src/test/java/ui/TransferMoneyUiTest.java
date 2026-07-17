package ui;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import com.codeborne.selenide.Configuration;
import constants.DepositLimits;
import api.requests.steps.CustomerContext;
import api.requests.steps.UserSteps;

@DisplayName("UI / Make a Transfer")
class TransferMoneyUiTest {

  @BeforeAll
  public static void setupSelenoid() {
    Configuration.remote = "http://localhost:4444/wd/hub";
    Configuration.baseUrl = "http://host.docker.internal:3000";
    Configuration.browser = "chrome";
    Configuration.browserVersion = "128.0";
    Configuration.browserSize = "1920x1080";
    Configuration.timeout = 15_000;
    Configuration.pageLoadTimeout = 60_000;

    ChromeOptions options = new ChromeOptions();
    options.setPageLoadStrategy(PageLoadStrategy.EAGER);
    options.addArguments("--disable-web-security");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--no-sandbox");
    options.addArguments("--user-data-dir=/tmp/chrome-nosecurity-" + UUID.randomUUID());
    options.setCapability("browserVersion", "128.0");
    options.setCapability("selenoid:options", Map.of(
        "enableVNC", true,
        "enableLog", true));
    Configuration.browserCapabilities = options;
  }

  @Test
  @DisplayName("позитивный перевод requests/ui/transfer_tests")
  void shouldTransferMoneyBetweenAccounts() {
    double depositAmount = 1000;
    double transferAmount = 50;
    CustomerContext user1 = CustomerContext.create()
        .withAccount()
        .withDeposit(depositAmount);
    CustomerContext user2 = CustomerContext.create()
        .withAccount();
    String user1AccountNumber = user1.account().getAccountNumber();
    String user2AccountNumber = user2.account().getAccountNumber();

    open("/");
    executeJavaScript("localStorage.setItem('authToken', arguments[0]);", user1.authToken());

    open("/dashboard");
    $(byText("User Dashboard")).shouldBe(visible);

    $(byText("🔄 Make a Transfer")).shouldBe(visible).click();

    $(".account-selector").shouldBe(visible)
        .selectOptionContainingText(user1AccountNumber);

    $(byAttribute("placeholder", "Enter recipient account number"))
        .shouldBe(visible)
        .setValue(user2AccountNumber);

    $(byAttribute("placeholder", "Enter amount"))
        .shouldBe(visible)
        .setValue(String.format(Locale.US, "%.2f", transferAmount));

    $("#confirmCheck").shouldBe(visible).click();

    $(byText("🚀 Send Transfer")).shouldBe(visible).shouldBe(enabled).click();

    Alert alert = switchTo().alert();
    String actualMessage = alert.getText();
    assertTrue(actualMessage.startsWith("✅ Successfully transferred $"));
    assertTrue(actualMessage.contains("to account " + user2AccountNumber));
    assertTrue(actualMessage.contains(String.format(Locale.US, "%.2f", transferAmount)));
    alert.accept();

    open("/transfer");
    String expectedSenderOption = String.format(Locale.US, "%s (Balance: $%.2f)",
        user1AccountNumber, depositAmount - transferAmount);
    $(".account-selector option[value='" + user1.accountId() + "']")
        .shouldBe(visible)
        .shouldHave(exactText(expectedSenderOption));

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
    String user1AccountNumber = user1.account().getAccountNumber();
    String user2AccountNumber = user2.account().getAccountNumber();

    open("/");
    executeJavaScript("localStorage.setItem('authToken', arguments[0]);", user1.authToken());

    open("/dashboard");
    $(byText("User Dashboard")).shouldBe(visible);

    $(byText("🔄 Make a Transfer")).shouldBe(visible).click();

    $(".account-selector").shouldBe(visible)
        .selectOptionContainingText(user1AccountNumber);

    $(byAttribute("placeholder", "Enter recipient account number"))
        .shouldBe(visible)
        .setValue(user2AccountNumber);

    $(byAttribute("placeholder", "Enter amount"))
        .shouldBe(visible)
        .setValue(String.valueOf(DepositLimits.ABOVE_TRANSFER_MAX));

    $("#confirmCheck").shouldBe(visible).click();

    $(byText("🚀 Send Transfer")).shouldBe(visible).shouldBe(enabled).click();

    Alert alert = switchTo().alert();
    String actualMessage = alert.getText();
    assertTrue(actualMessage.contains("Transfer amount cannot exceed 10000"));
    alert.accept();

    user1.assertBalance(startBalance);
    user2.assertBalance(0);
  }

  @Test
  @DisplayName("перевод без Confirm requests/ui/transfer_tests")
  void shouldRejectTransferWithoutConfirm() {
    double depositAmount = 1000;
    double transferAmount = 50;
    CustomerContext user1 = CustomerContext.create()
        .withAccount()
        .withDeposit(depositAmount);
    CustomerContext user2 = CustomerContext.create()
        .withAccount();
    String user1AccountNumber = user1.account().getAccountNumber();
    String user2AccountNumber = user2.account().getAccountNumber();

    open("/");
    executeJavaScript("localStorage.setItem('authToken', arguments[0]);", user1.authToken());

    open("/dashboard");
    $(byText("User Dashboard")).shouldBe(visible);

    $(byText("🔄 Make a Transfer")).shouldBe(visible).click();

    $(".account-selector").shouldBe(visible)
        .selectOptionContainingText(user1AccountNumber);

    $(byAttribute("placeholder", "Enter recipient account number"))
        .shouldBe(visible)
        .setValue(user2AccountNumber);

    $(byAttribute("placeholder", "Enter amount"))
        .shouldBe(visible)
        .setValue(String.format(Locale.US, "%.2f", transferAmount));

    $(byText("🚀 Send Transfer")).shouldBe(visible).shouldBe(enabled).click();

    Alert alert = switchTo().alert();
    String actualMessage = alert.getText();
    assertTrue(actualMessage.contains("Please fill all fields and confirm."));
    alert.accept();

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
    String user1AccountNumber = user1.account().getAccountNumber();

    UserSteps.transfer(user1.spec(), user1.transferRequestTo(user2, transferAmount));
    user1.assertBalance(depositAmount - transferAmount);
    user2.assertBalance(transferAmount);

    open("/");
    executeJavaScript("localStorage.setItem('authToken', arguments[0]);", user1.authToken());

    open("/dashboard");
    $(byText("User Dashboard")).shouldBe(visible);

    $(byText("🔄 Make a Transfer")).shouldBe(visible).click();
    $(byText("🔁 Transfer Again")).shouldBe(visible).click();

    $(byAttribute("placeholder", "Enter name to find transactions"))
        .shouldBe(visible)
        .setValue(user2.username());
    $(byText("🔍 Search Transactions")).shouldBe(visible).click();

    $$(".list-group-item")
        .findBy(text("TRANSFER_IN - $50.00"))
        .shouldBe(visible)
        .$(byText("🔁 Repeat"))
        .click();

    $(".modal-content").shouldBe(visible);
    $(".modal-body").shouldHave(text("Confirm transfer to Account ID:"));
    $(".modal-body input[type='number']").shouldHave(value("50"));

    $(".modal-body select").shouldBe(visible)
        .selectOptionContainingText(user1AccountNumber);

    $(".modal-body #confirmCheck").shouldBe(visible).click();
    $(".modal-footer").$(byText("🚀 Send Transfer")).shouldBe(enabled).click();

    Alert alert = switchTo().alert();
    String actualMessage = alert.getText();
    assertTrue(actualMessage.startsWith("✅ Transfer of $"));
    assertTrue(actualMessage.contains("successful"));
    assertTrue(actualMessage.contains("50"));
    alert.accept();

    $(byText("🔁 Transfer Again")).shouldBe(visible).click();
    $(byAttribute("placeholder", "Enter name to find transactions"))
        .shouldBe(visible)
        .setValue(user1.username());
    $(byText("🔍 Search Transactions")).shouldBe(visible).click();
    $$(".list-group-item")
        .filterBy(text("TRANSFER_OUT - $50.00"))
        .shouldHave(sizeGreaterThanOrEqual(2));

    user1.assertBalance(depositAmount - transferAmount * 2);
    user2.assertBalance(transferAmount * 2);
  }
}
