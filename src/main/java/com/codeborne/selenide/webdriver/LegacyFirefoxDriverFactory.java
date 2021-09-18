package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxOptions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public class LegacyFirefoxDriverFactory extends FirefoxDriverFactory {

  @Override
  public void setupWebdriverBinary() {
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public FirefoxOptions createCapabilities(Config config, Browser browser,
                                           @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
    FirefoxOptions firefoxOptions = super.createCapabilities(config, browser, proxy, browserDownloadsFolder);
    firefoxOptions.setLegacy(true);
    return firefoxOptions;
  }
}
