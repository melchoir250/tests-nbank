package ui;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
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
import generators.RandomData;
import requests.steps.CustomerContext;

@DisplayName("UI / Deposit Money")
class DepositAccountUiTest {

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
  @DisplayName("позитивный депозит requests/ui/deposit_tests")
  void shouldDepositMoneyToAccount() {
    CustomerContext customer = CustomerContext.create().withAccount();
    double amount = RandomData.depositAmount();

    open("/");
    executeJavaScript("localStorage.setItem('authToken', arguments[0]);", customer.authToken());

    open("/dashboard");
    $(byText("User Dashboard")).shouldBe(visible);

    $(byText("💰 Deposit Money")).shouldBe(visible).click();

    $(".account-selector").shouldBe(visible)
        .selectOptionContainingText(customer.account().getAccountNumber());

    $(byAttribute("placeholder", "Enter amount"))
        .shouldBe(visible)
        .setValue(String.format(Locale.US, "%.2f", amount));

    $(byText("💵 Deposit")).shouldBe(visible).shouldBe(enabled).click();

    Alert alert = switchTo().alert();
    String actualMessage = alert.getText();
    assertTrue(actualMessage.startsWith("✅ Successfully deposited $"));
    assertTrue(actualMessage.contains("to account " + customer.account().getAccountNumber()));
    assertTrue(actualMessage.contains(String.format(Locale.US, "%.2f", amount)));
    alert.accept();

    open("/deposit");
    String expectedOptionText = String.format(Locale.US, "%s (Balance: $%.2f)", customer.account().getAccountNumber(),
        amount);
    $(".account-selector option[value='" + customer.accountId() + "']")
        .shouldBe(visible)
        .shouldHave(exactText(expectedOptionText));

    customer.assertBalance(amount);
  }

  @Test
  @DisplayName("депозит сверх лимита requests/ui/deposit_tests")
  void shouldRejectDepositAboveMaximumLimit() {
    CustomerContext customer = CustomerContext.create().withAccount();
    String accountNumber = customer.account().getAccountNumber();

    open("/");
    executeJavaScript("localStorage.setItem('authToken', arguments[0]);", customer.authToken());

    open("/dashboard");
    $(byText("User Dashboard")).shouldBe(visible);

    $(byText("💰 Deposit Money")).shouldBe(visible).click();

    $(".account-selector").shouldBe(visible)
        .selectOptionContainingText(accountNumber);

    $(byAttribute("placeholder", "Enter amount"))
        .shouldBe(visible)
        .setValue(String.valueOf(DepositLimits.ABOVE_MAX));

    $(byText("💵 Deposit")).shouldBe(visible).shouldBe(enabled).click();

    Alert alert = switchTo().alert();
    String actualMessage = alert.getText();
    assertTrue(actualMessage.contains("Please deposit less or equal to 5000$."));
    alert.accept();

    webdriver().shouldHave(urlContaining("/deposit"));

    customer.assertBalance(0);
  }
}
