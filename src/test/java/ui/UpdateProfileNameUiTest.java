package ui;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.openqa.selenium.Alert;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import com.codeborne.selenide.Configuration;

import constants.ProfileLimits;
import generators.RandomData;
import requests.steps.CustomerContext;

@DisplayName("UI / Edit Profile")
class UpdateProfileNameUiTest {

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

  @ParameterizedTest
  @MethodSource("positiveNames")
  @DisplayName("изменение имени requests/ui/profile_tests")
  void shouldUpdateProfileName(String newName) {
    CustomerContext customer = CustomerContext.create();

    open("/");
    executeJavaScript("localStorage.setItem('authToken', arguments[0]);", customer.authToken());
    open("/dashboard");
    $(byText("User Dashboard")).shouldBe(visible);

    $(".profile-header").shouldBe(visible).click();

    $(byAttribute("placeholder", "Enter new name"))
        .shouldBe(visible)
        .setValue(newName);

    $(byText("💾 Save Changes")).shouldBe(visible).click();

    Alert alert = switchTo().alert();
    assertTrue(alert.getText().contains("Name updated successfully!"));
    alert.accept();

    open("/dashboard");
    refresh();
    $(".user-name").shouldHave(exactText(newName));
    $(".welcome-text").shouldHave(text(newName));

    customer.assertProfileName(newName);
  }

  static Stream<Arguments> positiveNames() {
    return Stream.of(
        Arguments.of(RandomData.validProfileName()),
        Arguments.of(ProfileLimits.VALID_ONE_CHAR),
        Arguments.of(ProfileLimits.VALID_MAX_LENGTH));
  }

  @ParameterizedTest
  @MethodSource("negativeNames")
  @DisplayName("негативный кейс изменение имени requests/ui/profile_tests")
  void shouldRejectInvalidProfileName(String newName) {
    CustomerContext customer = CustomerContext.create().withAccount();

    open("/");
    executeJavaScript("localStorage.setItem('authToken', arguments[0]);", customer.authToken());
    open("/dashboard");
    $(byText("User Dashboard")).shouldBe(visible);

    $(".profile-header").shouldBe(visible).click();

    $(byAttribute("placeholder", "Enter new name"))
        .shouldBe(visible)
        .setValue(newName);

    $(byText("💾 Save Changes")).shouldBe(visible).click();
    Alert alert = switchTo().alert();
    assertTrue(alert.getText().contains("Name must contain two words with letters only"));
    alert.accept();

    customer.assertProfileName(null);
  }

  static Stream<Arguments> negativeNames() {
    return Stream.of(
        Arguments.of(ProfileLimits.WITH_DIGIT),
        Arguments.of(ProfileLimits.WITH_SPECIAL),
        Arguments.of(ProfileLimits.THREE_WORDS));
  }
}
