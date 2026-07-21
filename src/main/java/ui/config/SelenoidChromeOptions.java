package ui.config;

import api.configs.Config;
import java.util.Map;
import java.util.UUID;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;

public final class SelenoidChromeOptions {
  private SelenoidChromeOptions() {
  }

  public static ChromeOptions create() {
    String browserVersion = Config.getProperty("browserVersion");

    ChromeOptions options = new ChromeOptions();
    options.setPageLoadStrategy(PageLoadStrategy.EAGER);
    options.addArguments(
        "--disable-web-security",
        "--disable-dev-shm-usage",
        "--no-sandbox",
        "--user-data-dir=/tmp/chrome-nosecurity-" + UUID.randomUUID());
    options.setCapability("browserVersion", browserVersion);
    options.setCapability("selenoid:options", Map.of(
        "enableVNC", true,
        "enableLog", true));
    return options;
  }
}
