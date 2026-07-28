package ui.config;

import api.configs.Config;
import com.codeborne.selenide.Configuration;

public final class UiTestConfig {
  private static final long TIMEOUT_MS = 15_000;
  private static final long PAGE_LOAD_TIMEOUT_MS = 60_000;

  private UiTestConfig() {
  }

  public static void apply() {
    Configuration.remote = Config.getProperty("uiRemote");
    Configuration.baseUrl = Config.getProperty("uiBaseUrl");
    Configuration.browser = Config.getProperty("browser");
    Configuration.browserSize = Config.getProperty("browserSize");
    Configuration.browserVersion = Config.getProperty("browserVersion");
    Configuration.timeout = TIMEOUT_MS;
    Configuration.pageLoadTimeout = PAGE_LOAD_TIMEOUT_MS;
    Configuration.browserCapabilities = SelenoidChromeOptions.create();
  }
}
